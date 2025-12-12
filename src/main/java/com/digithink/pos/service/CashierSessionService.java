package com.digithink.pos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.dto.CloseSessionRequestDTO;
import com.digithink.pos.dto.SessionDashboardDTO;
import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.Payment;
import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.ReturnHeader;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SessionCashCount;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.CounterType;
import com.digithink.pos.model.enumeration.PaymentMethodType;
import com.digithink.pos.model.enumeration.ReturnType;
import com.digithink.pos.model.enumeration.SessionStatus;
import com.digithink.pos.model.enumeration.SynchronizationStatus;
import com.digithink.pos.model.enumeration.TransactionStatus;
import com.digithink.pos.repository.CashierSessionRepository;
import com.digithink.pos.repository.PaymentMethodRepository;
import com.digithink.pos.repository.PaymentRepository;
import com.digithink.pos.repository.ReturnHeaderRepository;
import com.digithink.pos.repository.SalesHeaderRepository;
import com.digithink.pos.repository.SessionCashCountRepository;
import com.digithink.pos.repository._BaseRepository;
import com.digithink.pos.erp.repository.PaymentHeaderRepository;
import com.digithink.pos.erp.repository.PaymentLineRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CashierSessionService extends _BaseService<CashierSession, Long> {

	@Autowired
	private CashierSessionRepository cashierSessionRepository;
	
	@Autowired
	private SessionCashCountService sessionCashCountService;
	
	@Autowired
	private PaymentMethodRepository paymentMethodRepository;
	
	@Autowired
	private SalesHeaderRepository salesHeaderRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private SessionCashCountRepository sessionCashCountRepository;
	
	@Autowired
	private ReturnHeaderRepository returnHeaderRepository;

	@Autowired
	private PaymentHeaderRepository paymentHeaderRepository;

	@Autowired
	private PaymentLineRepository paymentLineRepository;

	@Override
	protected _BaseRepository<CashierSession, Long> getRepository() {
		return cashierSessionRepository;
	}

	/**
	 * Get the current open session for a cashier
	 */
	public Optional<CashierSession> getCurrentOpenSession(UserAccount cashier) {
		return cashierSessionRepository.findByCashierAndStatus(cashier, SessionStatus.OPENED);
	}

	/**
	 * Create a new cashier session with opening cash
	 * 
	 * @throws Exception
	 */
	public CashierSession openSession(UserAccount cashier, Double openingCash) throws Exception {
		// Check if cashier already has an open session
		Optional<CashierSession> existingSession = getCurrentOpenSession(cashier);
		if (existingSession.isPresent()) {
			throw new IllegalStateException("Cashier already has an open session");
		}

		CashierSession session = new CashierSession();
		session.setCashier(cashier);
		session.setOpeningCash(openingCash);
		session.setOpenedAt(LocalDateTime.now());
		session.setStatus(SessionStatus.OPENED);
		// Generate session number - count by day (e.g., SESSION251102001)
		LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		long count = cashierSessionRepository.countByOpenedAtGreaterThanEqual(todayStart);
		String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
		String sessionNumber = "SESSION" + dateStr + String.format("%03d", count + 1);
		session.setSessionNumber(sessionNumber);

		return save(session);
	}

	/**
	 * Close a cashier session (legacy method for backward compatibility)
	 * 
	 * @throws Exception
	 */
	public CashierSession closeSession(Long sessionId, Double actualCash, String notes) throws Exception {
		CloseSessionRequestDTO request = new CloseSessionRequestDTO();
		request.setActualCash(actualCash);
		request.setNotes(notes);
		request.setCashCountLines(new ArrayList<>());
		return closeSessionWithCashCount(sessionId, request);
	}

	/**
	 * Calculate real cash (expected cash) for a session
	 * This method calculates the expected cash amount based on transactions.
	 * Can be used for both OPENED and CLOSED sessions.
	 * 
	 * Formula: realCash = openingCash + cash sales - change given - simple returns (cash refunds)
	 * 
	 * Note: 
	 * - real_cash = realCash (calculated expected amount based on transactions)
	 * - actual_cash = posUserClosureCash (the actual cash counted by the POS user when closing)
	 * - Excludes PENDING and CANCELLED sales from calculations
	 */
	private Double calculateRealCash(CashierSession session) {
		Double realCash = session.getOpeningCash() != null ? session.getOpeningCash() : 0.0;

		// Get all sales for this session, excluding PENDING and CANCELLED
		List<SalesHeader> sales = salesHeaderRepository.findByCashierSession(session).stream()
			.filter(sale -> sale.getStatus() != TransactionStatus.PENDING && 
				sale.getStatus() != TransactionStatus.CANCELLED)
			.collect(Collectors.toList());

		for (SalesHeader sale : sales) {
			// Get all payments for this sale
			List<Payment> payments = paymentRepository.findBySalesHeader(sale);

			for (Payment payment : payments) {
				if (payment.getPaymentMethod() != null && 
					payment.getPaymentMethod().getType() == PaymentMethodType.CLIENT_ESPECES) {
					// Add cash payment amount
					if (payment.getTotalAmount() != null) {
						realCash += payment.getTotalAmount();
					}
				}
			}

			// Subtract change given
			if (sale.getChangeAmount() != null && sale.getChangeAmount() > 0) {
				realCash -= sale.getChangeAmount();
			}
		}

		// Subtract simple returns (cash refunds) - these reduce the cash in the till
		List<ReturnHeader> returns = returnHeaderRepository.findByCashierSession(session);
		for (ReturnHeader returnHeader : returns) {
			if (returnHeader.getReturnType() == ReturnType.SIMPLE_RETURN && 
				returnHeader.getTotalReturnAmount() != null) {
				realCash -= returnHeader.getTotalReturnAmount();
			}
		}

		return realCash;
	}

	/**
	 * Close a cashier session with detailed cash count lines
	 * 
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public CashierSession closeSessionWithCashCount(Long sessionId, CloseSessionRequestDTO request) throws Exception {
		CashierSession session = findById(sessionId)
				.orElseThrow(() -> new IllegalArgumentException("Session not found"));

		if (session.getStatus() != SessionStatus.OPENED) {
			throw new IllegalStateException("Session is not open");
		}
		
		// Check for pending tickets - session cannot be closed if there are pending tickets
		long pendingCount = salesHeaderRepository.findByCashierSessionAndStatus(
			session, 
			com.digithink.pos.model.enumeration.TransactionStatus.PENDING
		).size();
		
		if (pendingCount > 0) {
			throw new IllegalStateException("Cannot close session: There are " + pendingCount + " pending ticket(s). Please complete or cancel all pending tickets before closing the session.");
		}

		// Calculate real cash (expected cash based on sales)
		Double realCash = calculateRealCash(session);
		session.setRealCash(realCash);
		log.info("Real cash calculated: " + realCash + " for session: " + session.getSessionNumber());

		// Calculate POS user closure cash from cash count lines if provided
		Double calculatedPosUserCash = null;
		if (request.getCashCountLines() != null && !request.getCashCountLines().isEmpty()) {
			calculatedPosUserCash = request.getCashCountLines().stream()
				.mapToDouble(line -> (line.getDenominationValue() != null ? line.getDenominationValue() : 0.0) * 
					(line.getQuantity() != null ? line.getQuantity() : 0))
				.sum();
		}

		// Use provided actualCash or calculated from lines for POS user closure cash
		Double posUserClosureCash = request.getActualCash() != null ? request.getActualCash() : calculatedPosUserCash;
		session.setPosUserClosureCash(posUserClosureCash);

		session.setClosedAt(LocalDateTime.now());
		session.setStatus(SessionStatus.CLOSED);
		// Mark session as not synched so it can be exported to ERP
		session.setSynchronizationStatus(SynchronizationStatus.NOT_SYNCHED);

		session = save(session);
		log.info("Session closed: " + session.getSessionNumber() + 
			", Real Cash: " + realCash + ", POS User Closure Cash: " + posUserClosureCash);

		// Save cash count lines (counted by POS_USER)
		if (request.getCashCountLines() != null && !request.getCashCountLines().isEmpty()) {
			List<SessionCashCount> cashCounts = new ArrayList<>();
			for (CloseSessionRequestDTO.CashCountLineDTO lineDTO : request.getCashCountLines()) {
				SessionCashCount cashCount = new SessionCashCount();
				cashCount.setCashierSession(session);
				cashCount.setDenominationValue(lineDTO.getDenominationValue());
				cashCount.setQuantity(lineDTO.getQuantity());
				cashCount.setCounterType(CounterType.POS_USER);
				
				// Calculate line total
				Double lineTotal = (lineDTO.getDenominationValue() != null ? lineDTO.getDenominationValue() : 0.0) * 
					(lineDTO.getQuantity() != null ? lineDTO.getQuantity() : 0);
				cashCount.setLineTotal(lineTotal);
				
				// Set payment method if provided
				if (lineDTO.getPaymentMethodId() != null) {
					PaymentMethod paymentMethod = paymentMethodRepository.findById(lineDTO.getPaymentMethodId())
						.orElse(null);
					cashCount.setPaymentMethod(paymentMethod);
				}
				
				cashCount.setReferenceNumber(lineDTO.getReferenceNumber());
				cashCount.setNotes(lineDTO.getNotes());
				cashCount.setActive(true);
				
				cashCount = sessionCashCountService.save(cashCount);
				cashCounts.add(cashCount);
				log.info("Cash count line saved: " + cashCount.getDenominationValue() + " x " + cashCount.getQuantity());
			}
			log.info("Total cash count lines saved: " + cashCounts.size());
		}

		return session;
	}

	/**
	 * Verify session and set responsible closure cash (for responsible user)
	 * 
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public CashierSession verifySession(Long sessionId, Double responsibleClosureCash, String verificationNotes, UserAccount responsibleUser) throws Exception {
		CashierSession session = findById(sessionId)
				.orElseThrow(() -> new IllegalArgumentException("Session not found"));

		if (session.getStatus() != SessionStatus.CLOSED) {
			throw new IllegalStateException("Session must be closed before verification");
		}

		session.setResponsibleClosureCash(responsibleClosureCash);
		session.setVerifiedBy(responsibleUser);
		session.setVerifiedAt(LocalDateTime.now());
		session.setVerificationNotes(verificationNotes);
		session.setStatus(SessionStatus.TERMINATED);
		// Mark session as not synched so it can be exported to ERP (if not already synched)
		if (session.getSynchronizationStatus() == null || 
			session.getSynchronizationStatus() == SynchronizationStatus.NOT_SYNCHED) {
			session.setSynchronizationStatus(SynchronizationStatus.NOT_SYNCHED);
		}

		session = save(session);
		log.info("Session verified by responsible: " + session.getSessionNumber() + 
			", Responsible Closure Cash: " + responsibleClosureCash);

		return session;
	}

	/**
	 * Get session dashboard - list all sessions with statistics
	 */
	public List<SessionDashboardDTO> getSessionDashboard() {
		List<CashierSession> sessions = cashierSessionRepository.findAll();

		return sessions.stream().map(session -> {
			// Get sales count and total amount for this session (gross sales - no returns subtracted)
			// Exclude PENDING and CANCELLED sales
			List<SalesHeader> allSales = salesHeaderRepository.findByCashierSession(session);
			List<SalesHeader> sales = allSales.stream()
				.filter(sale -> sale.getStatus() != TransactionStatus.PENDING && 
					sale.getStatus() != TransactionStatus.CANCELLED)
				.collect(Collectors.toList());
			Long salesCount = (long) sales.size();
			Double totalSalesAmount = sales.stream()
				.mapToDouble(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : 0.0)
				.sum();

			// Get returns for this session
			List<ReturnHeader> returns = returnHeaderRepository.findByCashierSession(session);
			Long returnsCount = (long) returns.size();
			
			// Calculate total return amounts by type
			Double totalReturnsAmount = returns.stream()
				.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
				.sum();
			
			Double simpleReturnsAmount = returns.stream()
				.filter(ret -> ret.getReturnType() == ReturnType.SIMPLE_RETURN)
				.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
				.sum();
			
			Double voucherReturnsAmount = returns.stream()
				.filter(ret -> ret.getReturnType() == ReturnType.RETURN_VOUCHER)
				.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
				.sum();

			// Calculate real cash for this session (even if open)
			// Real cash = openingCash + cash sales - change given - simple returns
			Double calculatedRealCash = calculateRealCash(session);
			
			// For closed sessions, use stored realCash if available, otherwise calculate
			// For open sessions, always calculate
			Double realCash = session.getRealCash() != null && session.getStatus() == SessionStatus.CLOSED 
				? session.getRealCash() 
				: calculatedRealCash;

			return SessionDashboardDTO.fromEntity(session, salesCount, totalSalesAmount, 
				returnsCount, totalReturnsAmount, simpleReturnsAmount, voucherReturnsAmount, realCash);
		}).collect(Collectors.toList());
	}

	/**
	 * Get session details including cash count lines
	 */
	public Map<String, Object> getSessionDetails(Long sessionId) {
		CashierSession session = findById(sessionId)
				.orElseThrow(() -> new IllegalArgumentException("Session not found"));

		// Get sales for this session (gross sales - no returns subtracted)
		// Exclude PENDING and CANCELLED sales
		List<SalesHeader> allSales = salesHeaderRepository.findByCashierSession(session);
		List<SalesHeader> sales = allSales.stream()
			.filter(sale -> sale.getStatus() != TransactionStatus.PENDING && 
				sale.getStatus() != TransactionStatus.CANCELLED)
			.collect(Collectors.toList());
		Long salesCount = (long) sales.size();
		Double totalSalesAmount = sales.stream()
			.mapToDouble(sale -> sale.getTotalAmount() != null ? sale.getTotalAmount() : 0.0)
			.sum();

		// Get returns for this session
		List<ReturnHeader> returns = returnHeaderRepository.findByCashierSession(session);
		Long returnsCount = (long) returns.size();
		
		// Calculate total return amounts by type
		Double totalReturnsAmount = returns.stream()
			.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
			.sum();
		
		Double simpleReturnsAmount = returns.stream()
			.filter(ret -> ret.getReturnType() == ReturnType.SIMPLE_RETURN)
			.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
			.sum();
		
		Double voucherReturnsAmount = returns.stream()
			.filter(ret -> ret.getReturnType() == ReturnType.RETURN_VOUCHER)
			.mapToDouble(ret -> ret.getTotalReturnAmount() != null ? ret.getTotalReturnAmount() : 0.0)
			.sum();

		// Calculate real cash for this session (even if open)
		Double calculatedRealCash = calculateRealCash(session);
		Double realCash = session.getRealCash() != null && session.getStatus() == SessionStatus.CLOSED 
			? session.getRealCash() 
			: calculatedRealCash;

		// Get cash count lines
		List<SessionCashCount> cashCountLines = sessionCashCountRepository.findByCashierSession(session);

		// Get payment headers and lines for this session
		List<com.digithink.pos.erp.model.PaymentHeader> paymentHeaders = paymentHeaderRepository.findByCashierSession(session);
		List<com.digithink.pos.erp.model.PaymentLine> paymentLines = new ArrayList<>();
		for (com.digithink.pos.erp.model.PaymentHeader header : paymentHeaders) {
			paymentLines.addAll(paymentLineRepository.findByPaymentHeader(header));
		}

		// Build response
		Map<String, Object> response = new HashMap<>();
		response.put("session", SessionDashboardDTO.fromEntity(session, salesCount, totalSalesAmount,
			returnsCount, totalReturnsAmount, simpleReturnsAmount, voucherReturnsAmount, realCash));
		response.put("salesCount", salesCount);
		response.put("totalSalesAmount", totalSalesAmount); // Gross sales (sum of all sales)
		response.put("returnsCount", returnsCount);
		response.put("totalReturnsAmount", totalReturnsAmount);
		response.put("simpleReturnsAmount", simpleReturnsAmount);
		response.put("voucherReturnsAmount", voucherReturnsAmount);
		response.put("cashCountLines", cashCountLines);
		response.put("sales", sales);
		response.put("returns", returns);
		response.put("paymentHeaders", paymentHeaders);
		response.put("paymentLines", paymentLines);

		return response;
	}
}
