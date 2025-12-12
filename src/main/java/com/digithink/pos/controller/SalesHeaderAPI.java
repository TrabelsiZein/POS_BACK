package com.digithink.pos.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.dto.ProcessSaleRequestDTO;
import com.digithink.pos.model.Payment;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SalesLine;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.repository.PaymentRepository;
import com.digithink.pos.repository.SalesLineRepository;
import com.digithink.pos.security.CurrentUserProvider;
import com.digithink.pos.service.SalesHeaderService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("sales-header")
@Log4j2
public class SalesHeaderAPI extends _BaseController<SalesHeader, Long, SalesHeaderService> {

	@Autowired
	private CurrentUserProvider currentUserProvider;

	@Autowired
	private SalesLineRepository salesLineRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	/**
	 * Process complete sale (header + lines + payment + ticket)
	 */
	@PostMapping("/process-sale")
	public ResponseEntity<?> processSale(@RequestBody ProcessSaleRequestDTO request) {
		try {
			log.info("SalesHeaderAPI::processSale");
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			// Validate request
			if (request.getLines() == null || request.getLines().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Sale must have at least one item"));
			}

			if (request.getPayments() == null || request.getPayments().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("At least one payment method is required"));
			}

			// Validate each payment entry
			for (ProcessSaleRequestDTO.PaymentDTO payment : request.getPayments()) {
				if (payment.getPaymentMethodId() == null) {
					return ResponseEntity.badRequest()
							.body(createErrorResponse("Payment method is required for all payments"));
				}
				if (payment.getAmount() == null || payment.getAmount() <= 0) {
					return ResponseEntity.badRequest()
							.body(createErrorResponse("Payment amount must be greater than 0"));
				}
			}

			SalesHeader salesHeader = service.processCompleteSale(request, currentUser);

			// Fetch full sale data including lines and payments for receipt printing
			java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(salesHeader);
			java.util.List<Payment> payments = paymentRepository.findBySalesHeader(salesHeader);

			// Create response map with all sale data
			Map<String, Object> saleResponse = new HashMap<>();
			saleResponse.put("id", salesHeader.getId());
			saleResponse.put("salesNumber", salesHeader.getSalesNumber());
			saleResponse.put("salesDate", salesHeader.getSalesDate());
			saleResponse.put("subtotal", salesHeader.getSubtotal());
			saleResponse.put("taxAmount", salesHeader.getTaxAmount());
			saleResponse.put("discountAmount", salesHeader.getDiscountAmount());
			saleResponse.put("totalAmount", salesHeader.getTotalAmount());
			saleResponse.put("paidAmount", salesHeader.getPaidAmount());
			saleResponse.put("changeAmount", salesHeader.getChangeAmount());
			saleResponse.put("notes", salesHeader.getNotes());
			saleResponse.put("status", salesHeader.getStatus());
			saleResponse.put("createdByUser", salesHeader.getCreatedByUser());
			saleResponse.put("cashierSession", salesHeader.getCashierSession());
			saleResponse.put("customer", salesHeader.getCustomer());
			saleResponse.put("salesLines", salesLines);
			saleResponse.put("paymentHeaders", payments);

			return ResponseEntity.ok(saleResponse);

		} catch (IllegalArgumentException e) {
			log.error("SalesHeaderAPI::processSale:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("SalesHeaderAPI::processSale:state error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("SalesHeaderAPI::processSale:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Save a pending sale (without payments)
	 */
	@PostMapping("/save-pending")
	public ResponseEntity<?> savePendingSale(@RequestBody ProcessSaleRequestDTO request) {
		try {
			log.info("SalesHeaderAPI::savePendingSale");
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			// Validate request
			if (request.getLines() == null || request.getLines().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Sale must have at least one item"));
			}

			SalesHeader salesHeader = service.savePendingSale(request, currentUser);

			// Fetch sales lines for response
			java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(salesHeader);

			// Create response map
			Map<String, Object> saleResponse = new HashMap<>();
			saleResponse.put("id", salesHeader.getId());
			saleResponse.put("salesNumber", salesHeader.getSalesNumber());
			saleResponse.put("salesDate", salesHeader.getSalesDate());
			saleResponse.put("subtotal", salesHeader.getSubtotal());
			saleResponse.put("taxAmount", salesHeader.getTaxAmount());
			saleResponse.put("discountAmount", salesHeader.getDiscountAmount());
			saleResponse.put("totalAmount", salesHeader.getTotalAmount());
			saleResponse.put("status", salesHeader.getStatus());
			saleResponse.put("notes", salesHeader.getNotes());
			saleResponse.put("createdByUser", salesHeader.getCreatedByUser());
			saleResponse.put("cashierSession", salesHeader.getCashierSession());
			saleResponse.put("customer", salesHeader.getCustomer());
			saleResponse.put("salesLines", salesLines);

			return ResponseEntity.ok(saleResponse);

		} catch (IllegalArgumentException e) {
			log.error("SalesHeaderAPI::savePendingSale:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("SalesHeaderAPI::savePendingSale:state error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("SalesHeaderAPI::savePendingSale:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get pending sales for current session
	 */
	@GetMapping("/pending-sales")
	public ResponseEntity<?> getPendingSales() {
		try {
			log.info("SalesHeaderAPI::getPendingSales");
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			// Get current session - if no session, return empty list (session might be closed)
			java.util.Optional<com.digithink.pos.model.CashierSession> currentSessionOpt = service.getCurrentCashierSession(currentUser);
			
			if (!currentSessionOpt.isPresent()) {
				// No open session - return empty list (this is normal after closing a session)
				log.info("SalesHeaderAPI::getPendingSales: No open session found, returning empty list");
				return ResponseEntity.ok(new java.util.ArrayList<>());
			}
			
			com.digithink.pos.model.CashierSession currentSession = currentSessionOpt.get();

			// Get pending sales
			java.util.List<SalesHeader> pendingSales = service.getPendingSalesForSession(currentSession);

			// Sort by sales date descending (newest first)
			pendingSales.sort((s1, s2) -> {
				if (s1.getSalesDate() == null && s2.getSalesDate() == null)
					return 0;
				if (s1.getSalesDate() == null)
					return 1;
				if (s2.getSalesDate() == null)
					return -1;
				return s2.getSalesDate().compareTo(s1.getSalesDate()); // Descending order
			});

			// Fetch sales lines for each pending sale
			java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
			for (SalesHeader sale : pendingSales) {
				java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(sale);

				Map<String, Object> saleMap = new HashMap<>();
				saleMap.put("id", sale.getId());
				saleMap.put("salesNumber", sale.getSalesNumber());
				saleMap.put("salesDate", sale.getSalesDate());
				saleMap.put("subtotal", sale.getSubtotal());
				saleMap.put("taxAmount", sale.getTaxAmount());
				saleMap.put("discountAmount", sale.getDiscountAmount());
				saleMap.put("totalAmount", sale.getTotalAmount());
				saleMap.put("status", sale.getStatus());
				saleMap.put("notes", sale.getNotes());
				saleMap.put("createdByUser", sale.getCreatedByUser());
				saleMap.put("customer", sale.getCustomer());
				saleMap.put("salesLines", salesLines);

				result.add(saleMap);
			}

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			log.error("SalesHeaderAPI::getPendingSales:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Complete a pending sale (add payments)
	 */
	@PostMapping("/complete-pending/{id}")
	public ResponseEntity<?> completePendingSale(@PathVariable Long id, @RequestBody ProcessSaleRequestDTO request) {
		try {
			log.info("SalesHeaderAPI::completePendingSale: " + id);
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			// Validate request
			if (request.getPayments() == null || request.getPayments().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("At least one payment method is required"));
			}

			// Validate each payment entry
			for (ProcessSaleRequestDTO.PaymentDTO payment : request.getPayments()) {
				if (payment.getPaymentMethodId() == null) {
					return ResponseEntity.badRequest()
							.body(createErrorResponse("Payment method is required for all payments"));
				}
				if (payment.getAmount() == null || payment.getAmount() <= 0) {
					return ResponseEntity.badRequest()
							.body(createErrorResponse("Payment amount must be greater than 0"));
				}
			}

			SalesHeader salesHeader = service.completePendingSale(id, request, currentUser);

			// Fetch full sale data including lines and payments for receipt printing
			java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(salesHeader);
			java.util.List<Payment> payments = paymentRepository.findBySalesHeader(salesHeader);

			// Create response map with all sale data
			Map<String, Object> saleResponse = new HashMap<>();
			saleResponse.put("id", salesHeader.getId());
			saleResponse.put("salesNumber", salesHeader.getSalesNumber());
			saleResponse.put("salesDate", salesHeader.getSalesDate());
			saleResponse.put("subtotal", salesHeader.getSubtotal());
			saleResponse.put("taxAmount", salesHeader.getTaxAmount());
			saleResponse.put("discountAmount", salesHeader.getDiscountAmount());
			saleResponse.put("totalAmount", salesHeader.getTotalAmount());
			saleResponse.put("paidAmount", salesHeader.getPaidAmount());
			saleResponse.put("changeAmount", salesHeader.getChangeAmount());
			saleResponse.put("notes", salesHeader.getNotes());
			saleResponse.put("status", salesHeader.getStatus());
			saleResponse.put("createdByUser", salesHeader.getCreatedByUser());
			saleResponse.put("cashierSession", salesHeader.getCashierSession());
			saleResponse.put("customer", salesHeader.getCustomer());
			saleResponse.put("salesLines", salesLines);
			saleResponse.put("paymentHeaders", payments);

			return ResponseEntity.ok(saleResponse);

		} catch (IllegalArgumentException e) {
			log.error("SalesHeaderAPI::completePendingSale:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("SalesHeaderAPI::completePendingSale:state error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("SalesHeaderAPI::completePendingSale:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Cancel/Delete a pending sale
	 */
	@PostMapping("/cancel-pending/{id}")
	public ResponseEntity<?> cancelPendingSale(@PathVariable Long id) {
		try {
			log.info("SalesHeaderAPI::cancelPendingSale: " + id);
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			service.cancelPendingSale(id, currentUser);

			Map<String, Object> response = new HashMap<>();
			response.put("message", "Pending ticket cancelled successfully");
			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			log.error("SalesHeaderAPI::cancelPendingSale:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("SalesHeaderAPI::cancelPendingSale:state error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("SalesHeaderAPI::cancelPendingSale:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get tickets/sales with filters (for admin)
	 */
	@GetMapping("/history")
	public ResponseEntity<?> getTicketsHistory(
			@org.springframework.web.bind.annotation.RequestParam(required = false) String dateFrom,
			@org.springframework.web.bind.annotation.RequestParam(required = false) String dateTo,
			@org.springframework.web.bind.annotation.RequestParam(required = false) String status,
			@org.springframework.web.bind.annotation.RequestParam(required = false) String syncStatus,
			@org.springframework.web.bind.annotation.RequestParam(required = false) String paymentMethodId,
			@org.springframework.web.bind.annotation.RequestParam(required = false) String search) {
		try {
			log.info("SalesHeaderAPI::getTicketsHistory: dateFrom=" + dateFrom + ", dateTo=" + dateTo + ", status=" + status + ", syncStatus=" + syncStatus + ", paymentMethodId=" + paymentMethodId + ", search=" + search);

			java.util.List<SalesHeader> tickets = service.getTicketsHistory(dateFrom, dateTo, status, syncStatus, paymentMethodId, search);

			// Convert to response format with related data
			java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();
			for (SalesHeader ticket : tickets) {
				java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(ticket);
				java.util.List<Payment> payments = paymentRepository.findBySalesHeader(ticket);

				Map<String, Object> ticketMap = new HashMap<>();
				ticketMap.put("id", ticket.getId());
				ticketMap.put("salesNumber", ticket.getSalesNumber());
				ticketMap.put("salesDate", ticket.getSalesDate());
				ticketMap.put("subtotal", ticket.getSubtotal());
				ticketMap.put("taxAmount", ticket.getTaxAmount());
				ticketMap.put("discountAmount", ticket.getDiscountAmount());
				ticketMap.put("totalAmount", ticket.getTotalAmount());
				ticketMap.put("paidAmount", ticket.getPaidAmount());
				ticketMap.put("changeAmount", ticket.getChangeAmount());
				ticketMap.put("status", ticket.getStatus());
				ticketMap.put("notes", ticket.getNotes());
				ticketMap.put("createdByUser", ticket.getCreatedByUser());
				ticketMap.put("cashierSession", ticket.getCashierSession());
				ticketMap.put("customer", ticket.getCustomer());
				ticketMap.put("salesLinesCount", salesLines.size());
				ticketMap.put("paymentsCount", payments.size());
				ticketMap.put("synchronizationStatus", ticket.getSynchronizationStatus());
				ticketMap.put("erpNo", ticket.getErpNo());

				result.add(ticketMap);
			}

			return ResponseEntity.ok(result);
		} catch (Exception e) {
			log.error("SalesHeaderAPI::getTicketsHistory:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get ticket details by ID (for admin)
	 */
	@GetMapping("/{id}/details")
	public ResponseEntity<?> getTicketDetails(@PathVariable Long id) {
		try {
			log.info("SalesHeaderAPI::getTicketDetails: " + id);

			java.util.Optional<SalesHeader> ticketOpt = service.findById(id);
			if (!ticketOpt.isPresent()) {
				return ResponseEntity.notFound().build();
			}

			SalesHeader ticket = ticketOpt.get();
			java.util.List<SalesLine> salesLines = salesLineRepository.findBySalesHeader(ticket);
			java.util.List<Payment> payments = paymentRepository.findBySalesHeader(ticket);

			Map<String, Object> ticketMap = new HashMap<>();
			ticketMap.put("id", ticket.getId());
			ticketMap.put("salesNumber", ticket.getSalesNumber());
			ticketMap.put("salesDate", ticket.getSalesDate());
			ticketMap.put("subtotal", ticket.getSubtotal());
			ticketMap.put("taxAmount", ticket.getTaxAmount());
			ticketMap.put("discountAmount", ticket.getDiscountAmount());
			ticketMap.put("totalAmount", ticket.getTotalAmount());
			ticketMap.put("paidAmount", ticket.getPaidAmount());
			ticketMap.put("changeAmount", ticket.getChangeAmount());
			ticketMap.put("status", ticket.getStatus());
			ticketMap.put("notes", ticket.getNotes());
			ticketMap.put("completedDate", ticket.getCompletedDate());
			ticketMap.put("createdByUser", ticket.getCreatedByUser());
			ticketMap.put("cashierSession", ticket.getCashierSession());
			ticketMap.put("customer", ticket.getCustomer());
			ticketMap.put("synchronizationStatus", ticket.getSynchronizationStatus());
			ticketMap.put("erpNo", ticket.getErpNo());
			ticketMap.put("salesLines", salesLines);
			ticketMap.put("payments", payments);

			return ResponseEntity.ok(ticketMap);
		} catch (Exception e) {
			log.error("SalesHeaderAPI::getTicketDetails:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}
