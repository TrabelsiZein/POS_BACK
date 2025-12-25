package com.digithink.pos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.dto.ProcessSaleRequestDTO;
import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.GeneralSetup;
import com.digithink.pos.model.Item;
import com.digithink.pos.model.Payment;
import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SalesLine;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.PaymentMethodType;
import com.digithink.pos.model.enumeration.TransactionStatus;
import com.digithink.pos.repository.CashierSessionRepository;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository.GeneralSetupRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.PaymentMethodRepository;
import com.digithink.pos.repository.SalesHeaderRepository;
import com.digithink.pos.repository.SalesLineRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesHeaderService extends _BaseService<SalesHeader, Long> {

	@Autowired
	private SalesHeaderRepository salesHeaderRepository;

	@Autowired
	private SalesLineService salesLineService;

	@Autowired
	private SalesLineRepository salesLineRepository;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;

	@Autowired
	private CashierSessionRepository cashierSessionRepository;

	@Autowired
	private GeneralSetupRepository generalSetupRepository;

	@Autowired
	private CashierSessionService cashierSessionService;

	@Autowired
	private com.digithink.pos.service.ReturnVoucherService returnVoucherService;

	@Autowired
	private com.digithink.pos.erp.service.SessionExportService sessionExportService;

	@Override
	protected _BaseRepository<SalesHeader, Long> getRepository() {
		return salesHeaderRepository;
	}

	/**
	 * Get current cashier session for a user (helper method)
	 */
	public java.util.Optional<CashierSession> getCurrentCashierSession(UserAccount user) {
		return cashierSessionService.getCurrentOpenSession(user);
	}

	/**
	 * Process complete sale transactionally (header + lines + payment + ticket)
	 */
	@Transactional(rollbackFor = Exception.class)
	public SalesHeader processCompleteSale(ProcessSaleRequestDTO request, UserAccount currentUser) throws Exception {
		log.info("Processing complete sale for user: " + currentUser.getUsername());

		// Get current cashier session
		CashierSession currentSession = cashierSessionRepository
				.findByCashierAndStatus(currentUser, com.digithink.pos.model.enumeration.SessionStatus.OPENED)
				.orElseThrow(() -> new IllegalStateException("No open cashier session found"));

		// Generate sales number
		String salesNumber = generateSalesNumber();

		// Create sales header
		SalesHeader salesHeader = new SalesHeader();
		salesHeader.setSalesNumber(salesNumber);
		salesHeader.setSalesDate(LocalDateTime.now());
		salesHeader.setCreatedByUser(currentUser);
		salesHeader.setCashierSession(currentSession);
		salesHeader.setStatus(TransactionStatus.COMPLETED);
		salesHeader.setSubtotal(request.getSubtotal());
		salesHeader.setTaxAmount(request.getTaxAmount());
		salesHeader.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);
		salesHeader.setDiscountPercentage(request.getDiscountPercentage());
		salesHeader.setTotalAmount(request.getTotalAmount());
		salesHeader.setPaidAmount(request.getPaidAmount());
		salesHeader.setChangeAmount(request.getChangeAmount());
		salesHeader.setNotes(request.getNotes());
		salesHeader.setCompletedDate(LocalDateTime.now());

		// Set customer - use provided customer or passenger customer
		Customer customer = null;
		if (request.getCustomerId() != null) {
			customer = customerRepository.findById(request.getCustomerId()).orElse(null);
		}

		// If no customer provided, use passenger customer from GeneralSetup
		if (customer == null) {
			String passengerCustomerId = generalSetupRepository.findByCode("PASSENGER_CUSTOMER").get().getValeur();

			if (passengerCustomerId != null) {
				customer = customerRepository.findByCustomerCode(passengerCustomerId)
						.orElseThrow(() -> new IllegalStateException("Deafult customer not found"));
				if (customer != null) {
					log.info("Using passenger customer: " + customer.getCustomerCode() + " - " + customer.getName());
				} else {
					log.warn("Passenger customer ID found in GeneralSetup but customer not found: "
							+ passengerCustomerId);
				}
			} else {
				log.warn("PASSENGER_CUSTOMER not found in GeneralSetup");
			}
		}

		// Set customer (will be passenger customer if none provided)
		if (customer != null) {
			salesHeader.setCustomer(customer);
		} else {
			log.error("No customer assigned to sales header - customer was null and passenger customer not available");
		}

		// Save sales header
		salesHeader = save(salesHeader);
		log.info("Sales header created: " + salesHeader.getId());

		// Create sales lines
		List<SalesLine> salesLines = new ArrayList<>();
		for (ProcessSaleRequestDTO.SaleLineDTO lineDTO : request.getLines()) {
			Item item = itemRepository.findById(lineDTO.getItemId())
					.orElseThrow(() -> new IllegalArgumentException("Item not found: " + lineDTO.getItemId()));

			SalesLine salesLine = new SalesLine();
			salesLine.setSalesHeader(salesHeader);
			salesLine.setItem(item);
			salesLine.setQuantity(lineDTO.getQuantity());
			salesLine.setUnitPrice(lineDTO.getUnitPrice());
			salesLine.setLineTotal(lineDTO.getLineTotal());

			// Set discount fields
			if (lineDTO.getDiscountPercentage() != null) {
				salesLine.setDiscountPercentage(lineDTO.getDiscountPercentage());
			}
			if (lineDTO.getDiscountAmount() != null) {
				salesLine.setDiscountAmount(lineDTO.getDiscountAmount());
			}

			// Set VAT fields - use values from DTO if provided, otherwise calculate
			Integer vatPercent = lineDTO.getVatPercent() != null ? lineDTO.getVatPercent() : item.getDefaultVAT();
			salesLine.setVatPercent(vatPercent);

			if (lineDTO.getVatAmount() != null) {
				salesLine.setVatAmount(lineDTO.getVatAmount());
			} else {
				salesLine.setVatAmount(calculateVat(lineDTO.getLineTotal(), vatPercent));
			}

			if (lineDTO.getUnitPriceIncludingVat() != null) {
				salesLine.setUnitPriceIncludingVat(lineDTO.getUnitPriceIncludingVat());
			} else {
				salesLine.setUnitPriceIncludingVat(calculateUnitPriceIncludingVat(lineDTO.getUnitPrice(), vatPercent));
			}

			if (lineDTO.getLineTotalIncludingVat() != null) {
				salesLine.setLineTotalIncludingVat(lineDTO.getLineTotalIncludingVat());
			} else {
				salesLine.setLineTotalIncludingVat(
						lineDTO.getLineTotal() + (salesLine.getVatAmount() != null ? salesLine.getVatAmount() : 0.0));
			}

			salesLine = salesLineService.save(salesLine);
			salesLines.add(salesLine);
			log.info("Sales line created: " + salesLine.getId());
		}

		// Create payments (multiple payment methods support)
		List<Payment> payments = new ArrayList<>();
		Double totalPaid = 0.0;

		if (request.getPayments() == null || request.getPayments().isEmpty()) {
			throw new IllegalArgumentException("At least one payment method is required");
		}

		for (ProcessSaleRequestDTO.PaymentDTO paymentDTO : request.getPayments()) {
			if (paymentDTO.getPaymentMethodId() == null) {
				throw new IllegalArgumentException("Payment method ID is required for all payments");
			}
			if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0) {
				throw new IllegalArgumentException("Payment amount must be greater than 0");
			}

			PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId()).orElseThrow(
					() -> new IllegalArgumentException("Payment method not found: " + paymentDTO.getPaymentMethodId()));

			// Handle return voucher payment
			if (paymentMethod.getType() == com.digithink.pos.model.enumeration.PaymentMethodType.RETURN_VOUCHER) {
				// For return voucher, reference must contain voucher number
				if (paymentDTO.getReference() == null || paymentDTO.getReference().trim().isEmpty()) {
					throw new IllegalArgumentException("Voucher number is required for return voucher payment");
				}

				// Validate and use voucher
				com.digithink.pos.model.ReturnVoucher voucher = returnVoucherService
						.findByVoucherNumber(paymentDTO.getReference()).orElseThrow(() -> new IllegalArgumentException(
								"Return voucher not found: " + paymentDTO.getReference()));

				if (!returnVoucherService.isVoucherValid(voucher)) {
					throw new IllegalStateException("Return voucher is not valid (expired or fully used)");
				}

				double remainingAmount = returnVoucherService.getRemainingAmount(voucher);
				if (paymentDTO.getAmount() > remainingAmount) {
					throw new IllegalArgumentException("Payment amount (" + paymentDTO.getAmount()
							+ ") exceeds remaining voucher amount (" + remainingAmount + ")");
				}

				// Use the voucher amount
				returnVoucherService.useVoucherAmount(paymentDTO.getReference(), paymentDTO.getAmount());

				// Set payment reference to voucher number
				paymentDTO.setReference(paymentDTO.getReference());
			}

			validateAdditionalPaymentFields(paymentMethod, paymentDTO);

			Payment payment = new Payment();
			payment.setSalesHeader(salesHeader);
			payment.setPaymentMethod(paymentMethod);
			payment.setCreatedByUser(currentUser);
			payment.setStatus(TransactionStatus.COMPLETED);
			payment.setTotalAmount(paymentDTO.getAmount());
			payment.setPaymentDate(LocalDateTime.now());
			payment.setPaymentReference(paymentDTO.getReference());
			payment.setNotes(paymentDTO.getNotes());
			populateAdditionalPaymentFields(payment, paymentDTO);

			payment = paymentService.save(payment);
			payments.add(payment);
			totalPaid += paymentDTO.getAmount();
			log.info("Payment created: " + payment.getId() + " - Method: " + paymentMethod.getName() + ", Amount: "
					+ paymentDTO.getAmount());
		}

		// Update sales header with actual paid amounts
		salesHeader.setPaidAmount(totalPaid);
		double change = totalPaid - request.getTotalAmount();
		salesHeader.setChangeAmount(change > 0 ? change : 0.0);
		salesHeader = save(salesHeader);

		// Asynchronously create PaymentHeader and PaymentLine records for new payments
		// This stores payments immediately instead of grouping them during session sync
		try {
			sessionExportService.createPaymentHeadersAndLinesAsync(payments);
			log.info("Triggered async creation of payment headers/lines for {} payments", payments.size());
		} catch (Exception ex) {
			log.error("Failed to trigger async creation of payment headers/lines: " + ex.getMessage(), ex);
			// Don't fail the transaction if async call fails
		}

		// Note: Printing is now handled by the frontend (each POS terminal)
		// This allows multiple POS terminals to print independently
		log.info("Sale completed successfully: " + salesNumber + ". Printing handled by frontend.");

		return salesHeader;
	}

	/**
	 * Save a pending sale (without payments) - customer can continue later
	 */
	@Transactional(rollbackFor = Exception.class)
	public SalesHeader savePendingSale(ProcessSaleRequestDTO request, UserAccount currentUser) throws Exception {
		log.info("Saving pending sale for user: " + currentUser.getUsername());

		// Get current cashier session
		CashierSession currentSession = cashierSessionRepository
				.findByCashierAndStatus(currentUser, com.digithink.pos.model.enumeration.SessionStatus.OPENED)
				.orElseThrow(() -> new IllegalStateException("No open cashier session found"));

		// Generate sales number
		String salesNumber = generateSalesNumber();

		// Create sales header with PENDING status
		SalesHeader salesHeader = new SalesHeader();
		salesHeader.setSalesNumber(salesNumber);
		salesHeader.setSalesDate(LocalDateTime.now());
		salesHeader.setCreatedByUser(currentUser);
		salesHeader.setCashierSession(currentSession);
		salesHeader.setStatus(TransactionStatus.PENDING);
		salesHeader.setSubtotal(request.getSubtotal());
		salesHeader.setTaxAmount(request.getTaxAmount());
		salesHeader.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);
		salesHeader.setDiscountPercentage(request.getDiscountPercentage());
		salesHeader.setTotalAmount(request.getTotalAmount());
		salesHeader.setPaidAmount(0.0); // No payment yet
		salesHeader.setChangeAmount(0.0);
		salesHeader.setNotes(request.getNotes());
		// completedDate is null for pending sales

		// Set customer - use provided customer or passenger customer
		Customer customer = null;
		if (request.getCustomerId() != null) {
			customer = customerRepository.findById(request.getCustomerId()).orElse(null);
		}

		// If no customer provided, use passenger customer from GeneralSetup
		if (customer == null) {
			Long passengerCustomerId = generalSetupRepository.findByCode("PASSENGER_CUSTOMER").map(gs -> {
				try {
					return Long.parseLong(gs.getValeur());
				} catch (NumberFormatException e) {
					log.warn("Failed to parse PASSENGER_CUSTOMER ID: " + gs.getValeur());
					return null;
				}
			}).orElse(null);

			if (passengerCustomerId != null) {
				customer = customerRepository.findById(passengerCustomerId).orElse(null);
				if (customer != null) {
					log.info("Using passenger customer for pending sale: " + customer.getCustomerCode() + " - "
							+ customer.getName());
				} else {
					log.warn("Passenger customer ID found in GeneralSetup but customer not found: "
							+ passengerCustomerId);
				}
			} else {
				log.warn("PASSENGER_CUSTOMER not found in GeneralSetup");
			}
		}

		// Set customer (will be passenger customer if none provided)
		if (customer != null) {
			salesHeader.setCustomer(customer);
		} else {
			log.error(
					"No customer assigned to pending sales header - customer was null and passenger customer not available");
		}

		// Save sales header
		salesHeader = save(salesHeader);
		log.info("Pending sales header created: " + salesHeader.getId());

		// Create sales lines
		List<SalesLine> salesLines = new ArrayList<>();
		for (ProcessSaleRequestDTO.SaleLineDTO lineDTO : request.getLines()) {
			Item item = itemRepository.findById(lineDTO.getItemId())
					.orElseThrow(() -> new IllegalArgumentException("Item not found: " + lineDTO.getItemId()));

			SalesLine salesLine = new SalesLine();
			salesLine.setSalesHeader(salesHeader);
			salesLine.setItem(item);
			salesLine.setQuantity(lineDTO.getQuantity());
			salesLine.setUnitPrice(lineDTO.getUnitPrice());
			salesLine.setLineTotal(lineDTO.getLineTotal());

			// Set discount fields
			if (lineDTO.getDiscountPercentage() != null) {
				salesLine.setDiscountPercentage(lineDTO.getDiscountPercentage());
			}
			if (lineDTO.getDiscountAmount() != null) {
				salesLine.setDiscountAmount(lineDTO.getDiscountAmount());
			}

			// Set VAT fields - use values from DTO if provided, otherwise calculate
			Integer vatPercent = lineDTO.getVatPercent() != null ? lineDTO.getVatPercent() : item.getDefaultVAT();
			salesLine.setVatPercent(vatPercent);

			if (lineDTO.getVatAmount() != null) {
				salesLine.setVatAmount(lineDTO.getVatAmount());
			} else {
				salesLine.setVatAmount(calculateVat(lineDTO.getLineTotal(), vatPercent));
			}

			if (lineDTO.getUnitPriceIncludingVat() != null) {
				salesLine.setUnitPriceIncludingVat(lineDTO.getUnitPriceIncludingVat());
			} else {
				salesLine.setUnitPriceIncludingVat(calculateUnitPriceIncludingVat(lineDTO.getUnitPrice(), vatPercent));
			}

			if (lineDTO.getLineTotalIncludingVat() != null) {
				salesLine.setLineTotalIncludingVat(lineDTO.getLineTotalIncludingVat());
			} else {
				salesLine.setLineTotalIncludingVat(
						lineDTO.getLineTotal() + (salesLine.getVatAmount() != null ? salesLine.getVatAmount() : 0.0));
			}

			salesLine = salesLineService.save(salesLine);
			salesLines.add(salesLine);
			log.info("Pending sales line created: " + salesLine.getId());
		}

		// Note: No payments are created for pending sales
		// Payments will be added when the sale is completed
		log.info("Pending sale saved successfully: " + salesNumber);

		return salesHeader;
	}

	/**
	 * Complete a pending sale by adding payments
	 */
	@Transactional(rollbackFor = Exception.class)
	public SalesHeader completePendingSale(Long salesHeaderId, ProcessSaleRequestDTO request, UserAccount currentUser)
			throws Exception {
		log.info("Completing pending sale: " + salesHeaderId + " for user: " + currentUser.getUsername());

		// Get the pending sale
		SalesHeader salesHeader = findById(salesHeaderId)
				.orElseThrow(() -> new IllegalArgumentException("Pending sale not found: " + salesHeaderId));

		// Verify it's actually pending
		if (salesHeader.getStatus() != TransactionStatus.PENDING) {
			throw new IllegalStateException(
					"Sale is not in PENDING status. Current status: " + salesHeader.getStatus());
		}

		// Verify session is still open
		CashierSession currentSession = cashierSessionRepository
				.findByCashierAndStatus(currentUser, com.digithink.pos.model.enumeration.SessionStatus.OPENED)
				.orElseThrow(() -> new IllegalStateException("No open cashier session found"));

		if (!salesHeader.getCashierSession().getId().equals(currentSession.getId())) {
			throw new IllegalStateException("Pending sale does not belong to current session");
		}

		// Update totals (in case they changed)
		salesHeader.setSubtotal(request.getSubtotal());
		salesHeader.setTaxAmount(request.getTaxAmount());
		salesHeader.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : 0.0);
		salesHeader.setDiscountPercentage(request.getDiscountPercentage());
		salesHeader.setTotalAmount(request.getTotalAmount());
		if (request.getNotes() != null) {
			salesHeader.setNotes(request.getNotes());
		}

		// Delete existing sales lines (in case items were added/removed/modified)
		salesLineRepository.deleteBySalesHeader(salesHeader);
		log.info("Deleted all old sales lines for pending sale: " + salesHeader.getId());

		// Create new sales lines based on current cart (may have changed)
		List<SalesLine> salesLines = new ArrayList<>();
		for (ProcessSaleRequestDTO.SaleLineDTO lineDTO : request.getLines()) {
			Item item = itemRepository.findById(lineDTO.getItemId())
					.orElseThrow(() -> new IllegalArgumentException("Item not found: " + lineDTO.getItemId()));

			SalesLine salesLine = new SalesLine();
			salesLine.setSalesHeader(salesHeader);
			salesLine.setItem(item);
			salesLine.setQuantity(lineDTO.getQuantity());
			salesLine.setUnitPrice(lineDTO.getUnitPrice());
			salesLine.setLineTotal(lineDTO.getLineTotal());

			// Set discount fields
			if (lineDTO.getDiscountPercentage() != null) {
				salesLine.setDiscountPercentage(lineDTO.getDiscountPercentage());
			}
			if (lineDTO.getDiscountAmount() != null) {
				salesLine.setDiscountAmount(lineDTO.getDiscountAmount());
			}

			// Set VAT fields - use values from DTO if provided, otherwise calculate
			Integer vatPercent = lineDTO.getVatPercent() != null ? lineDTO.getVatPercent() : item.getDefaultVAT();
			salesLine.setVatPercent(vatPercent);

			if (lineDTO.getVatAmount() != null) {
				salesLine.setVatAmount(lineDTO.getVatAmount());
			} else {
				salesLine.setVatAmount(calculateVat(lineDTO.getLineTotal(), vatPercent));
			}

			if (lineDTO.getUnitPriceIncludingVat() != null) {
				salesLine.setUnitPriceIncludingVat(lineDTO.getUnitPriceIncludingVat());
			} else {
				salesLine.setUnitPriceIncludingVat(calculateUnitPriceIncludingVat(lineDTO.getUnitPrice(), vatPercent));
			}

			if (lineDTO.getLineTotalIncludingVat() != null) {
				salesLine.setLineTotalIncludingVat(lineDTO.getLineTotalIncludingVat());
			} else {
				salesLine.setLineTotalIncludingVat(
						lineDTO.getLineTotal() + (salesLine.getVatAmount() != null ? salesLine.getVatAmount() : 0.0));
			}

			salesLine = salesLineService.save(salesLine);
			salesLines.add(salesLine);
			log.info("Created new sales line: " + salesLine.getId() + " for pending sale completion");
		}

		// Create payments (multiple payment methods support)
		List<Payment> payments = new ArrayList<>();
		Double totalPaid = 0.0;

		if (request.getPayments() == null || request.getPayments().isEmpty()) {
			throw new IllegalArgumentException("At least one payment method is required");
		}

		for (ProcessSaleRequestDTO.PaymentDTO paymentDTO : request.getPayments()) {
			if (paymentDTO.getPaymentMethodId() == null) {
				throw new IllegalArgumentException("Payment method ID is required for all payments");
			}
			if (paymentDTO.getAmount() == null || paymentDTO.getAmount() <= 0) {
				throw new IllegalArgumentException("Payment amount must be greater than 0");
			}

			PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId()).orElseThrow(
					() -> new IllegalArgumentException("Payment method not found: " + paymentDTO.getPaymentMethodId()));

			// Handle return voucher payment
			if (paymentMethod.getType() == com.digithink.pos.model.enumeration.PaymentMethodType.RETURN_VOUCHER) {
				// For return voucher, reference must contain voucher number
				if (paymentDTO.getReference() == null || paymentDTO.getReference().trim().isEmpty()) {
					throw new IllegalArgumentException("Voucher number is required for return voucher payment");
				}

				// Validate and use voucher
				com.digithink.pos.model.ReturnVoucher voucher = returnVoucherService
						.findByVoucherNumber(paymentDTO.getReference()).orElseThrow(() -> new IllegalArgumentException(
								"Return voucher not found: " + paymentDTO.getReference()));

				if (!returnVoucherService.isVoucherValid(voucher)) {
					throw new IllegalStateException("Return voucher is not valid (expired or fully used)");
				}

				double remainingAmount = returnVoucherService.getRemainingAmount(voucher);
				if (paymentDTO.getAmount() > remainingAmount) {
					throw new IllegalArgumentException("Payment amount (" + paymentDTO.getAmount()
							+ ") exceeds remaining voucher amount (" + remainingAmount + ")");
				}

				// Use the voucher amount
				returnVoucherService.useVoucherAmount(paymentDTO.getReference(), paymentDTO.getAmount());

				// Set payment reference to voucher number
				paymentDTO.setReference(paymentDTO.getReference());
			}

			validateAdditionalPaymentFields(paymentMethod, paymentDTO);

			Payment payment = new Payment();
			payment.setSalesHeader(salesHeader);
			payment.setPaymentMethod(paymentMethod);
			payment.setCreatedByUser(currentUser);
			payment.setStatus(TransactionStatus.COMPLETED);
			payment.setTotalAmount(paymentDTO.getAmount());
			payment.setPaymentDate(LocalDateTime.now());
			payment.setPaymentReference(paymentDTO.getReference());
			payment.setNotes(paymentDTO.getNotes());
			populateAdditionalPaymentFields(payment, paymentDTO);

			payment = paymentService.save(payment);
			payments.add(payment);
			totalPaid += paymentDTO.getAmount();
			log.info("Payment created for pending sale: " + payment.getId() + " - Method: " + paymentMethod.getName()
					+ ", Amount: " + paymentDTO.getAmount());
		}

		// Update sales header with payment info and mark as completed
		salesHeader.setPaidAmount(totalPaid);
		double change = totalPaid - request.getTotalAmount();
		salesHeader.setChangeAmount(change > 0 ? change : 0.0);
		salesHeader.setStatus(TransactionStatus.COMPLETED);
		salesHeader.setCompletedDate(LocalDateTime.now());
		salesHeader = save(salesHeader);

		log.info("Pending sale completed successfully: " + salesHeader.getSalesNumber());

		// Asynchronously create PaymentHeader and PaymentLine records for new payments
		// This stores payments immediately instead of grouping them during session sync
		try {
			sessionExportService.createPaymentHeadersAndLinesAsync(payments);
			log.info("Triggered async creation of payment headers/lines for {} payments", payments.size());
		} catch (Exception ex) {
			log.error("Failed to trigger async creation of payment headers/lines: " + ex.getMessage(), ex);
			// Don't fail the transaction if async call fails
		}

		return salesHeader;
	}

	/**
	 * Get all pending sales for current session
	 */
	public List<SalesHeader> getPendingSalesForSession(CashierSession session) {
		return salesHeaderRepository.findByCashierSessionAndStatus(session, TransactionStatus.PENDING);
	}

	private void validateAdditionalPaymentFields(PaymentMethod paymentMethod,
			ProcessSaleRequestDTO.PaymentDTO paymentDTO) {
		boolean requireTitle = Boolean.TRUE.equals(paymentMethod.getRequireTitleNumber());
		boolean requireDueDate = Boolean.TRUE.equals(paymentMethod.getRequireDueDate());
		boolean requireDrawer = Boolean.TRUE.equals(paymentMethod.getRequireDrawerName());
		boolean requireBank = Boolean.TRUE.equals(paymentMethod.getRequireIssuingBank());

		if (requireTitle && isBlank(paymentDTO.getTitleNumber())) {
			throw new IllegalArgumentException(
					"Title number is required for payment method: " + paymentMethod.getName());
		}

		// Validate title number length if configured in GeneralSetup
		if (requireTitle && !isBlank(paymentDTO.getTitleNumber())) {
			Integer requiredLength = getTitleNumberLengthForPaymentType(paymentMethod.getType());
			if (requiredLength != null) {
				String titleNumber = paymentDTO.getTitleNumber().trim();
				if (titleNumber.length() != requiredLength) {
					throw new IllegalArgumentException(
							"Title number must be exactly " + requiredLength + " characters for payment method: "
									+ paymentMethod.getName() + ". Provided: " + titleNumber.length() + " characters.");
				}
			}
		}

		if (requireDueDate && paymentDTO.getDueDate() == null) {
			throw new IllegalArgumentException("Due date is required for payment method: " + paymentMethod.getName());
		}
		if (requireDrawer && isBlank(paymentDTO.getDrawerName())) {
			throw new IllegalArgumentException(
					"Drawer name is required for payment method: " + paymentMethod.getName());
		}
		if (requireBank && isBlank(paymentDTO.getIssuingBank())) {
			throw new IllegalArgumentException(
					"Issuing bank is required for payment method: " + paymentMethod.getName());
		}
	}

	private void populateAdditionalPaymentFields(Payment payment, ProcessSaleRequestDTO.PaymentDTO paymentDTO) {
		payment.setTitleNumber(paymentDTO.getTitleNumber());
		payment.setDueDate(paymentDTO.getDueDate());
		payment.setDrawerName(paymentDTO.getDrawerName());
		payment.setIssuingBank(paymentDTO.getIssuingBank());
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * Get the required title number length for a payment method type from
	 * GeneralSetup. Returns null if not configured (backward compatible).
	 * 
	 * Configuration pattern: PAYMENT_METHOD_{TYPE}_TITLE_NUMBER_LENGTH Example:
	 * PAYMENT_METHOD_CLIENT_CHEQUE_TITLE_NUMBER_LENGTH = "7"
	 * 
	 * @param paymentMethodType The payment method type
	 * @return The required length as Integer, or null if not configured
	 */
	private Integer getTitleNumberLengthForPaymentType(PaymentMethodType paymentMethodType) {
		if (paymentMethodType == null) {
			return null;
		}

		String configCode = "PAYMENT_METHOD_" + paymentMethodType.name() + "_TITLE_NUMBER_LENGTH";
		java.util.Optional<GeneralSetup> setup = generalSetupRepository.findByCode(configCode);

		if (setup.isPresent() && setup.get().getValeur() != null) {
			try {
				return Integer.parseInt(setup.get().getValeur().trim());
			} catch (NumberFormatException e) {
				log.warn("Invalid title number length configuration for {}: {}", configCode, setup.get().getValeur());
				return null;
			}
		}

		return null;
	}

	/**
	 * Count pending sales for session
	 */
	public long countPendingSalesForSession(CashierSession session) {
		return salesHeaderRepository.findByCashierSessionAndStatus(session, TransactionStatus.PENDING).size();
	}

	/**
	 * Cancel/Delete a pending sale
	 */
	@Transactional(rollbackFor = Exception.class)
	public void cancelPendingSale(Long salesHeaderId, UserAccount currentUser) throws Exception {
		log.info("Cancelling pending sale: " + salesHeaderId + " for user: " + currentUser.getUsername());

		// Get the pending sale
		SalesHeader salesHeader = findById(salesHeaderId)
				.orElseThrow(() -> new IllegalArgumentException("Pending sale not found: " + salesHeaderId));

		// Verify it's actually pending
		if (salesHeader.getStatus() != TransactionStatus.PENDING) {
			throw new IllegalStateException(
					"Sale is not in PENDING status. Current status: " + salesHeader.getStatus());
		}

		// Verify session is still open and belongs to current user
		CashierSession currentSession = cashierSessionRepository
				.findByCashierAndStatus(currentUser, com.digithink.pos.model.enumeration.SessionStatus.OPENED)
				.orElseThrow(() -> new IllegalStateException("No open cashier session found"));

		if (!salesHeader.getCashierSession().getId().equals(currentSession.getId())) {
			throw new IllegalStateException("Pending sale does not belong to current session");
		}

		// Change status to CANCELLED
		salesHeader.setStatus(TransactionStatus.CANCELLED);
		save(salesHeader);

		log.info("Pending sale cancelled successfully: " + salesHeader.getSalesNumber());
	}

	/**
	 * Generate unique sales number with location prefix and count by day
	 */
	private String generateSalesNumber() {
		// Get default location from GeneralSetup
		String locationCode = generalSetupRepository.findByCode("DEFAULT_LOCATION").map(gs -> gs.getValeur())
				.orElse("LOC001");

		// Count sales for today only
		LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
		long count = salesHeaderRepository.countBySalesDateGreaterThanEqual(todayStart);

		// Format: LOC001251102043 (locationCode + YY + MM + DD + sequence)
		String dateStr = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
		return locationCode + dateStr + String.format("%03d", count + 1);
	}

	/**
	 * Get tickets history with filters
	 */
	public java.util.List<SalesHeader> getTicketsHistory(String dateFromStr, String dateToStr, String statusStr,
			String syncStatusStr, String paymentMethodIdStr, String searchStr) {
		// Parse date from (start of day)
		java.time.LocalDateTime dateFrom = null;
		if (dateFromStr != null && !dateFromStr.trim().isEmpty()) {
			try {
				java.time.LocalDate date = java.time.LocalDate.parse(dateFromStr);
				dateFrom = date.atStartOfDay();
			} catch (Exception e) {
				log.warn("Invalid dateFrom format: " + dateFromStr);
			}
		}

		// Parse date to (end of day)
		java.time.LocalDateTime dateTo = null;
		if (dateToStr != null && !dateToStr.trim().isEmpty()) {
			try {
				java.time.LocalDate date = java.time.LocalDate.parse(dateToStr);
				dateTo = date.atTime(23, 59, 59);
			} catch (Exception e) {
				log.warn("Invalid dateTo format: " + dateToStr);
			}
		}

		// Parse status
		TransactionStatus status = null;
		if (statusStr != null && !statusStr.trim().isEmpty() && !statusStr.equalsIgnoreCase("all")) {
			try {
				status = TransactionStatus.valueOf(statusStr.toUpperCase());
			} catch (Exception e) {
				log.warn("Invalid status: " + statusStr);
			}
		}

		// Parse sync status
		com.digithink.pos.model.enumeration.SynchronizationStatus syncStatus = null;
		if (syncStatusStr != null && !syncStatusStr.trim().isEmpty() && !syncStatusStr.equalsIgnoreCase("all")) {
			try {
				syncStatus = com.digithink.pos.model.enumeration.SynchronizationStatus
						.valueOf(syncStatusStr.toUpperCase());
			} catch (Exception e) {
				log.warn("Invalid syncStatus: " + syncStatusStr);
			}
		}

		// Parse payment method ID
		Long paymentMethodId = null;
		if (paymentMethodIdStr != null && !paymentMethodIdStr.trim().isEmpty()
				&& !paymentMethodIdStr.equalsIgnoreCase("all")) {
			try {
				paymentMethodId = Long.parseLong(paymentMethodIdStr);
			} catch (Exception e) {
				log.warn("Invalid paymentMethodId format: " + paymentMethodIdStr);
			}
		}

		// Capture variables for lambda (effectively final)
		final java.time.LocalDateTime finalDateFrom = dateFrom;
		final java.time.LocalDateTime finalDateTo = dateTo;
		final TransactionStatus finalStatus = status;
		final com.digithink.pos.model.enumeration.SynchronizationStatus finalSyncStatus = syncStatus;
		final Long finalPaymentMethodId = paymentMethodId;
		final String finalSearchStr = searchStr;

		// Build specification for filtering
		org.springframework.data.jpa.domain.Specification<SalesHeader> spec = (root, query, criteriaBuilder) -> {
			java.util.List<javax.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

			// Date range filter
			if (finalDateFrom != null && finalDateTo != null) {
				predicates.add(criteriaBuilder.between(root.get("salesDate"), finalDateFrom, finalDateTo));
			} else if (finalDateFrom != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("salesDate"), finalDateFrom));
			} else if (finalDateTo != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("salesDate"), finalDateTo));
			}

			// Status filter
			if (finalStatus != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), finalStatus));
			}

			// Sync status filter
			if (finalSyncStatus != null) {
				predicates.add(criteriaBuilder.equal(root.get("synchronizationStatus"), finalSyncStatus));
			}

			// Payment method filter (filter by tickets that have payments with this payment
			// method)
			if (finalPaymentMethodId != null) {
				// Use subquery to find tickets with payments matching the payment method
				javax.persistence.criteria.Subquery<Long> paymentSubquery = query.subquery(Long.class);
				javax.persistence.criteria.Root<com.digithink.pos.model.Payment> paymentRoot = paymentSubquery
						.from(com.digithink.pos.model.Payment.class);
				paymentSubquery.select(paymentRoot.get("salesHeader").get("id"));
				paymentSubquery.where(criteriaBuilder.and(
						criteriaBuilder.equal(paymentRoot.get("paymentMethod").get("id"), finalPaymentMethodId),
						criteriaBuilder.equal(paymentRoot.get("salesHeader").get("id"), root.get("id"))));
				predicates.add(criteriaBuilder.exists(paymentSubquery));
			}

			// Search filter (by sales number or customer name)
			if (finalSearchStr != null && !finalSearchStr.trim().isEmpty()) {
				String searchPattern = "%" + finalSearchStr.trim().toLowerCase() + "%";
				javax.persistence.criteria.Predicate salesNumberPredicate = criteriaBuilder
						.like(criteriaBuilder.lower(root.get("salesNumber")), searchPattern);
				javax.persistence.criteria.Predicate customerNamePredicate = criteriaBuilder
						.like(criteriaBuilder.lower(root.join("customer").get("name")), searchPattern);
				predicates.add(criteriaBuilder.or(salesNumberPredicate, customerNamePredicate));
			}

			return criteriaBuilder.and(predicates.toArray(new javax.persistence.criteria.Predicate[0]));
		};

		// Find all matching tickets, sorted by date descending (newest first)
		java.util.List<SalesHeader> tickets = salesHeaderRepository.findAll(spec, org.springframework.data.domain.Sort
				.by(org.springframework.data.domain.Sort.Direction.DESC, "salesDate"));

		return tickets;
	}

	/**
	 * Calculate VAT amount from line total and VAT percentage
	 */
	private Double calculateVat(Double lineTotal, Integer vatPercentage) {
		if (lineTotal == null || vatPercentage == null || vatPercentage == 0) {
			return 0.0;
		}
		// VAT calculation: lineTotal * (vatPercentage / 100)
		return lineTotal * (vatPercentage / 100.0);
	}

	/**
	 * Calculate unit price including VAT
	 */
	private Double calculateUnitPriceIncludingVat(Double unitPrice, Integer vatPercentage) {
		if (unitPrice == null) {
			return 0.0;
		}
		if (vatPercentage == null || vatPercentage == 0) {
			return unitPrice;
		}
		// Unit price including VAT: unitPrice * (1 + vatPercentage / 100)
		return unitPrice * (1.0 + (vatPercentage / 100.0));
	}
}
