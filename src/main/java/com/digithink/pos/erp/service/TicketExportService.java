package com.digithink.pos.erp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import com.digithink.pos.erp.dynamicsnav.client.DynamicsNavRestClient;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderHeaderDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderLineDTO;
import com.digithink.pos.erp.enumeration.ErpCommunicationStatus;
import com.digithink.pos.erp.enumeration.ErpSyncOperation;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SalesLine;
import com.digithink.pos.model.enumeration.SynchronizationStatus;
import com.digithink.pos.model.enumeration.TransactionStatus;
import com.digithink.pos.repository.SalesHeaderRepository;
import com.digithink.pos.repository.SalesLineRepository;
import com.digithink.pos.service.GeneralSetupService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketExportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TicketExportService.class);

	private final DynamicsNavRestClient dynamicsNavRestClient;
	private final SalesHeaderRepository salesHeaderRepository;
	private final SalesLineRepository salesLineRepository;
	private final GeneralSetupService generalSetupService;
	private final ErpCommunicationService communicationService;
	private final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * Export tickets to Dynamics NAV
	 */
	@Transactional
	public void exportTickets() {
		LOGGER.info("Starting ticket export to Dynamics NAV");

		// Find all completed tickets that are not totally synched
		List<SalesHeader> ticketsToSync = salesHeaderRepository.findByStatusAndSynchronizationStatusNot(
				TransactionStatus.COMPLETED, SynchronizationStatus.TOTALLY_SYNCHED);

		if (ticketsToSync.isEmpty()) {
			LOGGER.info("No tickets to export");
			return;
		}

		LOGGER.info("Found {} tickets to export", ticketsToSync.size());

		int successCount = 0;
		int errorCount = 0;

		for (SalesHeader ticket : ticketsToSync) {
			try {
				exportTicket(ticket);
				successCount++;
			} catch (Exception ex) {
				LOGGER.error("Failed to export ticket {}: {}", ticket.getSalesNumber(), ex.getMessage(), ex);
				errorCount++;
			}
		}

		LOGGER.info("Ticket export completed. Success: {}, Errors: {}", successCount, errorCount);
	}

	/**
	 * Export a single ticket to Dynamics NAV
	 */
	@Transactional
	public void exportTicket(SalesHeader ticket) {
		LOGGER.info("Exporting ticket: {}", ticket.getSalesNumber());

		// If not synched at all, create header first
		if (ticket.getSynchronizationStatus() == SynchronizationStatus.NOT_SYNCHED) {
			exportTicketHeader(ticket);
		}

		// Export lines that are not synched
		exportTicketLines(ticket);

		// Check if all lines are synched, then update header status
		List<SalesLine> allLines = salesLineRepository.findBySalesHeader(ticket);
		boolean allLinesSynched = allLines.stream().allMatch(SalesLine::getSynched);

		if (allLinesSynched && !allLines.isEmpty()) {
			// Update POS_Order to true in NAV
			if (ticket.getErpNo() != null) {
				LocalDateTime updateStart = LocalDateTime.now();
				try {
					dynamicsNavRestClient.updateSalesOrderHeaderPosOrder(ticket.getErpNo(), true);

					// Log successful POS_Order update
					DynamicsNavSalesOrderHeaderDTO updatePayload = new DynamicsNavSalesOrderHeaderDTO();
					updatePayload.setPosOrder(true);
					communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, updatePayload, null,
							ErpCommunicationStatus.SUCCESS, ticket.getErpNo(), null, updateStart, LocalDateTime.now());
				} catch (HttpClientErrorException | HttpServerErrorException ex) {
					// Extract error response body for logging
					String errorResponseBody = ex.getResponseBodyAsString();
					Object errorResponse = null;
					if (errorResponseBody != null && !errorResponseBody.trim().isEmpty()) {
						errorResponse = errorResponseBody;
					}

					// Log error operation with error response body
					DynamicsNavSalesOrderHeaderDTO updatePayload = new DynamicsNavSalesOrderHeaderDTO();
					updatePayload.setPosOrder(true);
					communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, updatePayload, errorResponse,
							ErpCommunicationStatus.ERROR, ticket.getErpNo(), ex.getMessage(), updateStart,
							LocalDateTime.now());
					LOGGER.error("Failed to update POS_Order for ticket {}: {}", ticket.getSalesNumber(),
							ex.getMessage(), ex);
					// Don't throw - we'll try again next time
				} catch (Exception ex) {
					// Log error operation
					DynamicsNavSalesOrderHeaderDTO updatePayload = new DynamicsNavSalesOrderHeaderDTO();
					updatePayload.setPosOrder(true);
					communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, updatePayload, null,
							ErpCommunicationStatus.ERROR, ticket.getErpNo(), ex.getMessage(), updateStart,
							LocalDateTime.now());
					LOGGER.error("Failed to update POS_Order for ticket {}: {}", ticket.getSalesNumber(),
							ex.getMessage(), ex);
					// Don't throw - we'll try again next time
				}
			}

			// Update status to totally synched
			ticket.setSynchronizationStatus(SynchronizationStatus.TOTALLY_SYNCHED);
			salesHeaderRepository.save(ticket);
			LOGGER.info("Ticket {} fully synchronized", ticket.getSalesNumber());
		}
	}

	/**
	 * Export ticket header to Dynamics NAV
	 */
	private void exportTicketHeader(SalesHeader ticket) {
		LocalDateTime start = LocalDateTime.now();
		DynamicsNavSalesOrderHeaderDTO headerDTO = new DynamicsNavSalesOrderHeaderDTO();

		// Set customer information
		if (ticket.getCustomer() != null) {
			headerDTO.setSellToCustomerNo(ticket.getCustomer().getCustomerCode());
//			headerDTO.setSellToCustomerName(ticket.getCustomer().getName());
		}

		// Set responsibility center and location from GeneralSetup
		String responsibilityCenter = generalSetupService.findValueByCode("RESPONSIBILITY_CENTER");
		if (responsibilityCenter != null) {
			headerDTO.setResponsibilityCenter(responsibilityCenter);
		}

		String locationCode = generalSetupService.findValueByCode("DEFAULT_LOCATION");
		if (locationCode != null) {
			headerDTO.setLocationCode(locationCode);
		}

		// Set posting date
		headerDTO.setPostingDate(ticket.getSalesDate().toLocalDate());

		// Set Fence_No to cashier session ID
		if (ticket.getCashierSession() != null) {
			headerDTO.setFenceNo(ticket.getCashierSession().getSessionNumber());
		}

		// Set POS document number
		headerDTO.setPosDocumentNo(ticket.getSalesNumber());

		// Set discount percentage
		if (ticket.getDiscountPercentage() != null) {
			headerDTO.setDiscountPercent(ticket.getDiscountPercentage());
		}

		// Set POS_Order to false initially
		headerDTO.setPosOrder(false);

		try {
			// Create header in NAV
			DynamicsNavSalesOrderHeaderDTO createdHeader = dynamicsNavRestClient.createSalesOrderHeader(headerDTO);

			// Extract Document_No from response
			if (createdHeader != null && createdHeader.getDocumentNo() != null) {
				ticket.setErpNo(createdHeader.getDocumentNo());
				ticket.setSynchronizationStatus(SynchronizationStatus.PARTIALLY_SYNCHED);
				salesHeaderRepository.save(ticket);

				// Log successful operation
				communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, headerDTO, createdHeader,
						ErpCommunicationStatus.SUCCESS, createdHeader.getDocumentNo(), null, start,
						LocalDateTime.now());

				LOGGER.info("Ticket header {} exported to NAV with document number: {}", ticket.getSalesNumber(),
						createdHeader.getDocumentNo());
			} else {
				String errorMsg = "Failed to get document number from NAV response";
				// Log error operation
				communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, headerDTO, createdHeader,
						ErpCommunicationStatus.ERROR, null, errorMsg, start, LocalDateTime.now());
				LOGGER.error("Failed to get document number from NAV response for ticket {}", ticket.getSalesNumber());
				throw new RuntimeException(errorMsg);
			}
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			// Extract error response body for logging
			String errorResponseBody = ex.getResponseBodyAsString();
			Object errorResponse = null;
			if (errorResponseBody != null && !errorResponseBody.trim().isEmpty()) {
				try {
					// Try to parse as JSON for better display
					errorResponse = errorResponseBody;
				} catch (Exception parseEx) {
					errorResponse = errorResponseBody;
				}
			}

			// Extract clean error message (without full payload)
			String cleanErrorMessage = extractCleanErrorMessage(ex, errorResponseBody);

			// Log error operation with error response body
			// errorMessage contains short message, full response is in response_payload
			communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, headerDTO, errorResponse,
					ErpCommunicationStatus.ERROR, null, cleanErrorMessage, start, LocalDateTime.now());
			throw ex;
		} catch (Exception ex) {
			// Log error operation
			communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, headerDTO, null,
					ErpCommunicationStatus.ERROR, null, ex.getMessage(), start, LocalDateTime.now());
			throw ex;
		}
	}

	/**
	 * Export ticket lines to Dynamics NAV
	 */
	private void exportTicketLines(SalesHeader ticket) {
		if (ticket.getErpNo() == null) {
			LOGGER.warn("Cannot export lines for ticket {} - no ERP document number", ticket.getSalesNumber());
			return;
		}

		List<SalesLine> lines = salesLineRepository.findBySalesHeader(ticket);
		List<SalesLine> unsynchedLines = lines.stream().filter(line -> !Boolean.TRUE.equals(line.getSynched()))
				.toList();

		if (unsynchedLines.isEmpty()) {
			LOGGER.info("All lines for ticket {} are already synched", ticket.getSalesNumber());
			return;
		}

		LOGGER.info("Exporting {} lines for ticket {}", unsynchedLines.size(), ticket.getSalesNumber());

		int lineNo = 10000; // Starting line number
		for (SalesLine line : unsynchedLines) {
			LocalDateTime lineStart = LocalDateTime.now();
			DynamicsNavSalesOrderLineDTO lineDTO = new DynamicsNavSalesOrderLineDTO();
			lineDTO.setDocumentNo(ticket.getErpNo());
			lineDTO.setLineNo(lineNo);
			lineDTO.setNo(line.getItem().getItemCode());
			lineDTO.setQuantity(line.getQuantity().doubleValue());
			lineDTO.setUnitPrice(line.getUnitPrice());
			// Type is read-only in NAV, so set it to null to exclude it from request
			if (line.getDiscountPercentage() != null) {
				lineDTO.setLineDiscountPercent(line.getDiscountPercentage());
			}

			try {
				// Create line in NAV
				DynamicsNavSalesOrderLineDTO createdLine = dynamicsNavRestClient.createSalesOrderLine(lineDTO);

				// Mark line as synched
				line.setSynched(true);
				salesLineRepository.save(line);

				// Log successful operation
				communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, lineDTO, createdLine,
						ErpCommunicationStatus.SUCCESS, ticket.getErpNo() + "-" + lineNo, null, lineStart,
						LocalDateTime.now());

				lineNo += 10000; // Increment by 10000 for next line
				LOGGER.info("Exported line {} for ticket {}", line.getId(), ticket.getSalesNumber());
			} catch (HttpClientErrorException | HttpServerErrorException ex) {
				// Extract error response body for logging
				String errorResponseBody = ex.getResponseBodyAsString();
				Object errorResponse = null;
				if (errorResponseBody != null && !errorResponseBody.trim().isEmpty()) {
					errorResponse = errorResponseBody;
				}

				// Extract clean error message (without full payload)
				String cleanErrorMessage = extractCleanErrorMessage(ex, errorResponseBody);

				// Log error operation with error response body
				// errorMessage contains short message, full response is in response_payload
				communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, lineDTO, errorResponse,
						ErpCommunicationStatus.ERROR, ticket.getErpNo() + "-" + lineNo, cleanErrorMessage, lineStart,
						LocalDateTime.now());
				LOGGER.error("Failed to export line {} for ticket {}: {}", line.getId(), ticket.getSalesNumber(),
						ex.getMessage(), ex);
				// Continue with next line
			} catch (Exception ex) {
				// Log error operation
				communicationService.logOperation(ErpSyncOperation.EXPORT_TICKET, lineDTO, null,
						ErpCommunicationStatus.ERROR, ticket.getErpNo() + "-" + lineNo, ex.getMessage(), lineStart,
						LocalDateTime.now());
				LOGGER.error("Failed to export line {} for ticket {}: {}", line.getId(), ticket.getSalesNumber(),
						ex.getMessage(), ex);
				// Continue with next line
			}
		}
	}

	/**
	 * Extract a clean error message from HTTP exception without the full JSON
	 * payload
	 */
	private String extractCleanErrorMessage(HttpStatusCodeException ex, String errorResponseBody) {
		// Try to extract just the error message from JSON response
		if (errorResponseBody != null && !errorResponseBody.trim().isEmpty()) {
			try {
				JsonNode errorNode = objectMapper.readTree(errorResponseBody);

				// Handle array of errors: [{"error": {...}}]
				if (errorNode.isArray() && errorNode.size() > 0) {
					JsonNode firstError = errorNode.get(0);
					if (firstError.has("error")) {
						JsonNode error = firstError.get("error");
						if (error.has("message")) {
							return ex.getStatusCode() + " " + error.get("message").asText();
						}
					}
				}

				// Handle single error object: {"error": {...}}
				if (errorNode.has("error")) {
					JsonNode error = errorNode.get("error");
					if (error.has("message")) {
						return ex.getStatusCode() + " " + error.get("message").asText();
					}
				}
			} catch (Exception parseEx) {
				LOGGER.debug("Failed to parse error response body: {}", parseEx.getMessage());
			}
		}

		// Fallback to status code and status text
		return ex.getStatusCode() + " " + (ex.getStatusText() != null ? ex.getStatusText() : "Unknown error");
	}
}
