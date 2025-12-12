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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.dto.ProcessReturnRequestDTO;
import com.digithink.pos.model.ReturnHeader;
import com.digithink.pos.model.ReturnLine;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.repository.ReturnHeaderRepository;
import com.digithink.pos.repository.ReturnLineRepository;
import com.digithink.pos.repository.SalesHeaderRepository;
import com.digithink.pos.repository.SalesLineRepository;
import com.digithink.pos.security.CurrentUserProvider;
import com.digithink.pos.service.ReturnHeaderService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("return-header")
@Log4j2
public class ReturnHeaderAPI extends _BaseController<ReturnHeader, Long, ReturnHeaderService> {

	@Autowired
	private CurrentUserProvider currentUserProvider;

	@Autowired
	private SalesHeaderRepository salesHeaderRepository;

	@Autowired
	private SalesLineRepository salesLineRepository;

	@Autowired
	private ReturnLineRepository returnLineRepository;

	@Autowired
	private ReturnHeaderRepository returnHeaderRepository;

	/**
	 * Get ticket details by ticket number (for return processing)
	 */
	@GetMapping("/ticket-details")
	public ResponseEntity<?> getTicketDetails(@RequestParam String ticketNumber) {
		try {
			log.info("ReturnHeaderAPI::getTicketDetails: ticketNumber=" + ticketNumber);

			SalesHeader salesHeader = salesHeaderRepository.findBySalesNumber(ticketNumber)
					.orElseThrow(() -> new IllegalArgumentException("Ticket not found: " + ticketNumber));

			// Check if ticket can be returned
			boolean canReturn = service.canReturnTicket(salesHeader);
			if (!canReturn) {
				return ResponseEntity.badRequest().body(createErrorResponse("This ticket is too old to be returned"));
			}

			// Get sales lines
			java.util.List<com.digithink.pos.model.SalesLine> salesLines = salesLineRepository
					.findBySalesHeader(salesHeader);

			// Get all return headers for this sales header (there can be multiple returns
			// for the same ticket)
			java.util.List<com.digithink.pos.model.ReturnHeader> returnHeaders = returnHeaderRepository
					.findAllByOriginalSalesHeader(salesHeader);

			// Calculate returned quantities for each sales line
			// Map: salesLineId -> total returned quantity
			Map<Long, Integer> returnedQuantities = new HashMap<>();

			for (com.digithink.pos.model.ReturnHeader returnHeader : returnHeaders) {
				java.util.List<com.digithink.pos.model.ReturnLine> returnLines = returnLineRepository
						.findByReturnHeader(returnHeader);
				for (com.digithink.pos.model.ReturnLine returnLine : returnLines) {
					Long originalSalesLineId = returnLine.getOriginalSalesLine().getId();
					int returnedQty = returnLine.getQuantity();
					returnedQuantities.put(originalSalesLineId,
							returnedQuantities.getOrDefault(originalSalesLineId, 0) + returnedQty);
				}
			}

			// Build sales lines with remaining quantities (only include lines with
			// remaining > 0)
			java.util.List<Map<String, Object>> salesLinesWithRemaining = new java.util.ArrayList<>();

			for (com.digithink.pos.model.SalesLine salesLine : salesLines) {
				int originalQuantity = salesLine.getQuantity();
				int returnedQuantity = returnedQuantities.getOrDefault(salesLine.getId(), 0);
				int remainingQuantity = originalQuantity - returnedQuantity;

				// Only include lines that still have remaining quantity to return
				if (remainingQuantity > 0) {
					Map<String, Object> lineData = new HashMap<>();
					lineData.put("id", salesLine.getId());
					lineData.put("item", salesLine.getItem());
					lineData.put("quantity", originalQuantity); // Original purchased quantity
					lineData.put("unitPrice", salesLine.getUnitPrice());
					lineData.put("lineTotal", salesLine.getLineTotal());
					lineData.put("discountPercentage", salesLine.getDiscountPercentage());
					lineData.put("discountAmount", salesLine.getDiscountAmount());
					lineData.put("vatAmount", salesLine.getVatAmount());
					lineData.put("vatPercent", salesLine.getVatPercent());
					lineData.put("unitPriceIncludingVat", salesLine.getUnitPriceIncludingVat());
					lineData.put("lineTotalIncludingVat", salesLine.getLineTotalIncludingVat());
					lineData.put("returnedQuantity", returnedQuantity); // Already returned
					lineData.put("remainingQuantity", remainingQuantity); // Can still be returned

					salesLinesWithRemaining.add(lineData);
				}
			}

			// Create response
			Map<String, Object> response = new HashMap<>();
			response.put("ticket", salesHeader);
			response.put("salesLines", salesLinesWithRemaining);
			response.put("canReturn", canReturn);
			response.put("isSimpleReturnEnabled", service.isSimpleReturnEnabled());

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			log.error("ReturnHeaderAPI::getTicketDetails:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error("ReturnHeaderAPI::getTicketDetails:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}

	/**
	 * Process return
	 */
	@PostMapping("/process-return")
	public ResponseEntity<?> processReturn(@RequestBody ProcessReturnRequestDTO request) {
		try {
			log.info("ReturnHeaderAPI::processReturn");
			UserAccount currentUser = currentUserProvider.getCurrentUser();

			// Validate request
			if (request.getTicketNumber() == null || request.getTicketNumber().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Ticket number is required"));
			}

			if (request.getReturnType() == null) {
				return ResponseEntity.badRequest().body(createErrorResponse("Return type is required"));
			}

			if (request.getReturnLines() == null || request.getReturnLines().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("At least one return line is required"));
			}

			// Validate return type
			if (request.getReturnType() == com.digithink.pos.model.enumeration.ReturnType.SIMPLE_RETURN
					&& !service.isSimpleReturnEnabled()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Simple return is not enabled"));
			}

			ReturnHeader returnHeader = service.processReturn(request, currentUser);

			// Fetch full return data including lines
			java.util.List<ReturnLine> returnLines = returnLineRepository.findByReturnHeader(returnHeader);

			// Create response map with all return data (avoiding circular references)
			Map<String, Object> returnResponse = new HashMap<>();
			returnResponse.put("id", returnHeader.getId());
			returnResponse.put("returnNumber", returnHeader.getReturnNumber());
			returnResponse.put("returnDate", returnHeader.getReturnDate());
			returnResponse.put("returnType", returnHeader.getReturnType());
			returnResponse.put("totalReturnAmount", returnHeader.getTotalReturnAmount());
			returnResponse.put("notes", returnHeader.getNotes());
			returnResponse.put("status", returnHeader.getStatus());

			// Add original sales header info (avoiding circular references)
			if (returnHeader.getOriginalSalesHeader() != null) {
				Map<String, Object> originalSalesHeaderInfo = new HashMap<>();
				originalSalesHeaderInfo.put("id", returnHeader.getOriginalSalesHeader().getId());
				originalSalesHeaderInfo.put("salesNumber", returnHeader.getOriginalSalesHeader().getSalesNumber());
				originalSalesHeaderInfo.put("salesDate", returnHeader.getOriginalSalesHeader().getSalesDate());
				originalSalesHeaderInfo.put("totalAmount", returnHeader.getOriginalSalesHeader().getTotalAmount());
				returnResponse.put("originalSalesHeader", originalSalesHeaderInfo);
			}

			// Convert return lines to maps with item details (avoiding circular references)
			java.util.List<Map<String, Object>> returnLinesData = new java.util.ArrayList<>();
			for (ReturnLine line : returnLines) {
				Map<String, Object> lineData = new HashMap<>();
				lineData.put("id", line.getId());
				lineData.put("quantity", line.getQuantity());
				lineData.put("unitPrice", line.getUnitPrice());
				lineData.put("unitPriceIncludingVat", line.getUnitPriceIncludingVat());
				lineData.put("lineTotal", line.getLineTotal());
				lineData.put("lineTotalIncludingVat", line.getLineTotalIncludingVat());
				lineData.put("notes", line.getNotes());
				lineData.put("synched", line.getSynched());

				// Add item details
				if (line.getItem() != null) {
					Map<String, Object> itemData = new HashMap<>();
					itemData.put("id", line.getItem().getId());
					itemData.put("name", line.getItem().getName());
					itemData.put("itemCode", line.getItem().getItemCode());
					lineData.put("item", itemData);
				}

				returnLinesData.add(lineData);
			}
			returnResponse.put("returnLines", returnLinesData);

			// Add return voucher info if exists (avoiding circular references)
			if (returnHeader.getReturnVoucher() != null) {
				Map<String, Object> voucherInfo = new HashMap<>();
				voucherInfo.put("id", returnHeader.getReturnVoucher().getId());
				voucherInfo.put("voucherNumber", returnHeader.getReturnVoucher().getVoucherNumber());
				voucherInfo.put("voucherDate", returnHeader.getReturnVoucher().getVoucherDate());
				voucherInfo.put("voucherAmount", returnHeader.getReturnVoucher().getVoucherAmount());
				voucherInfo.put("expiryDate", returnHeader.getReturnVoucher().getExpiryDate());
				voucherInfo.put("status", returnHeader.getReturnVoucher().getStatus());
				voucherInfo.put("usedAmount", returnHeader.getReturnVoucher().getUsedAmount());
				voucherInfo.put("notes", returnHeader.getReturnVoucher().getNotes());
				if (returnHeader.getReturnVoucher().getCustomer() != null) {
					voucherInfo.put("customerId", returnHeader.getReturnVoucher().getCustomer().getId());
					voucherInfo.put("customerName", returnHeader.getReturnVoucher().getCustomer().getName());
				}
				returnResponse.put("returnVoucher", voucherInfo);
			}

			return ResponseEntity.ok(returnResponse);

		} catch (IllegalArgumentException e) {
			log.error("ReturnHeaderAPI::processReturn:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			log.error("ReturnHeaderAPI::processReturn:state error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error("ReturnHeaderAPI::processReturn:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}

	/**
	 * Get return details by ID
	 */
	@GetMapping("/{id}/details")
	public ResponseEntity<?> getReturnDetails(@PathVariable Long id) {
		try {
			log.info("ReturnHeaderAPI::getReturnDetails: id=" + id);

			ReturnHeader returnHeader = service.getReturnDetails(id);

			// Get return lines
			java.util.List<ReturnLine> returnLines = returnLineRepository.findByReturnHeader(returnHeader);

			// Create response (avoiding circular references)
			Map<String, Object> response = new HashMap<>();

			// Build return header info without circular references
			Map<String, Object> returnHeaderInfo = new HashMap<>();
			returnHeaderInfo.put("id", returnHeader.getId());
			returnHeaderInfo.put("returnNumber", returnHeader.getReturnNumber());
			returnHeaderInfo.put("returnDate", returnHeader.getReturnDate());
			returnHeaderInfo.put("returnType", returnHeader.getReturnType());
			returnHeaderInfo.put("totalReturnAmount", returnHeader.getTotalReturnAmount());
			returnHeaderInfo.put("notes", returnHeader.getNotes());
			returnHeaderInfo.put("status", returnHeader.getStatus());
			returnHeaderInfo.put("synchronizationStatus", returnHeader.getSynchronizationStatus());
			returnHeaderInfo.put("erpNo", returnHeader.getErpNo());

			if (returnHeader.getOriginalSalesHeader() != null) {
				Map<String, Object> originalSalesHeaderInfo = new HashMap<>();
				originalSalesHeaderInfo.put("id", returnHeader.getOriginalSalesHeader().getId());
				originalSalesHeaderInfo.put("salesNumber", returnHeader.getOriginalSalesHeader().getSalesNumber());
				originalSalesHeaderInfo.put("salesDate", returnHeader.getOriginalSalesHeader().getSalesDate());
				originalSalesHeaderInfo.put("totalAmount", returnHeader.getOriginalSalesHeader().getTotalAmount());
				returnHeaderInfo.put("originalSalesHeader", originalSalesHeaderInfo);
			}

			if (returnHeader.getReturnVoucher() != null) {
				Map<String, Object> voucherInfo = new HashMap<>();
				voucherInfo.put("id", returnHeader.getReturnVoucher().getId());
				voucherInfo.put("voucherNumber", returnHeader.getReturnVoucher().getVoucherNumber());
				voucherInfo.put("voucherDate", returnHeader.getReturnVoucher().getVoucherDate());
				voucherInfo.put("voucherAmount", returnHeader.getReturnVoucher().getVoucherAmount());
				voucherInfo.put("expiryDate", returnHeader.getReturnVoucher().getExpiryDate());
				voucherInfo.put("status", returnHeader.getReturnVoucher().getStatus());
				voucherInfo.put("usedAmount", returnHeader.getReturnVoucher().getUsedAmount());
				returnHeaderInfo.put("returnVoucher", voucherInfo);
			}

			// Convert return lines to maps with item details (avoiding circular references)
			java.util.List<Map<String, Object>> returnLinesData = new java.util.ArrayList<>();
			for (ReturnLine line : returnLines) {
				Map<String, Object> lineData = new HashMap<>();
				lineData.put("id", line.getId());
				lineData.put("quantity", line.getQuantity());
				lineData.put("unitPrice", line.getUnitPrice());
				lineData.put("unitPriceIncludingVat", line.getUnitPriceIncludingVat());
				lineData.put("lineTotal", line.getLineTotal());
				lineData.put("lineTotalIncludingVat", line.getLineTotalIncludingVat());
				lineData.put("notes", line.getNotes());
				lineData.put("synched", line.getSynched());

				// Add item details
				if (line.getItem() != null) {
					Map<String, Object> itemData = new HashMap<>();
					itemData.put("id", line.getItem().getId());
					itemData.put("name", line.getItem().getName());
					itemData.put("itemCode", line.getItem().getItemCode());
					lineData.put("item", itemData);
				}

				returnLinesData.add(lineData);
			}

			response.put("returnHeader", returnHeaderInfo);
			response.put("returnLines", returnLinesData);

			return ResponseEntity.ok(response);

		} catch (IllegalArgumentException e) {
			log.error("ReturnHeaderAPI::getReturnDetails:validation error: " + e.getMessage(), e);
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error("ReturnHeaderAPI::getReturnDetails:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}
}
