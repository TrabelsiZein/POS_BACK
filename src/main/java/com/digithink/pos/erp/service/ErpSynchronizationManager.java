package com.digithink.pos.erp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpOperationResult;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpTicketDTO;
import com.digithink.pos.erp.enumeration.ErpCommunicationStatus;
import com.digithink.pos.erp.enumeration.ErpSyncOperation;
import com.digithink.pos.erp.spi.ErpConnector;
 
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpSynchronizationManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpSynchronizationManager.class);

	private final ErpConnector erpConnector;
	private final ErpCommunicationService communicationService;

	public List<ErpItemFamilyDTO> pullItemFamilies(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_ITEM_FAMILIES, filter,
				() -> erpConnector.fetchItemFamilies(filter));
	}

	public List<ErpItemSubFamilyDTO> pullItemSubFamilies(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_ITEM_SUBFAMILIES, filter,
				() -> erpConnector.fetchItemSubFamilies(filter));
	}

	public List<ErpItemDTO> pullItems(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_ITEMS, filter,
				() -> erpConnector.fetchItems(filter));
	}

	public List<ErpItemBarcodeDTO> pullItemBarcodes(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_ITEM_BARCODES, filter,
				() -> erpConnector.fetchItemBarcodes(filter));
	}

	public List<ErpLocationDTO> pullLocations(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_LOCATIONS, filter,
				() -> erpConnector.fetchLocations(filter));
	}

	public List<ErpCustomerDTO> pullCustomers(ErpSyncFilter filter) {
		return executePullOperation(ErpSyncOperation.IMPORT_CUSTOMERS, filter,
				() -> erpConnector.fetchCustomers(filter));
	}

	public ErpOperationResult pushCustomer(ErpCustomerDTO customerDTO) {
		return executePushOperation(ErpSyncOperation.EXPORT_CUSTOMER, customerDTO,
				() -> erpConnector.pushCustomer(customerDTO));
	}

	public ErpOperationResult pushTicket(ErpTicketDTO ticketDTO) {
		return executePushOperation(ErpSyncOperation.EXPORT_TICKET, ticketDTO,
				() -> erpConnector.pushTicket(ticketDTO));
	}

	private <T> List<T> executePullOperation(ErpSyncOperation operation, ErpSyncFilter filter,
			OperationExecutor<List<T>> executor) {
		LocalDateTime start = LocalDateTime.now();
		try {
			List<T> result = executor.execute();
			communicationService.logOperation(operation, filter, result,
					ErpCommunicationStatus.SUCCESS, null, null, start, LocalDateTime.now());
			return result;
		} catch (Exception ex) {
			LOGGER.error("ERP pull operation {} failed: {}", operation, ex.getMessage(), ex);
			
			// Extract error response body if available (from HTTP exceptions)
			Object errorResponse = extractErrorResponseBody(ex);
			
			communicationService.logOperation(operation, filter, errorResponse,
					ErpCommunicationStatus.ERROR, null, ex.getMessage(), start, LocalDateTime.now());
			throw ex;
		}
	}

	private ErpOperationResult executePushOperation(ErpSyncOperation operation, Object payload,
			OperationExecutor<ErpOperationResult> executor) {
		LocalDateTime start = LocalDateTime.now();
		try {
			ErpOperationResult result = executor.execute();
			ErpCommunicationStatus status = result.isSuccess()
					? ErpCommunicationStatus.SUCCESS
					: ErpCommunicationStatus.ERROR;
			communicationService.logOperation(operation, payload, result,
					status, result.getExternalReference(), result.getMessage(),
					start, LocalDateTime.now());
			return result;
		} catch (Exception ex) {
			LOGGER.error("ERP push operation {} failed: {}", operation, ex.getMessage(), ex);
			
			// Extract error response body if available (from HTTP exceptions)
			Object errorResponse = extractErrorResponseBody(ex);
			
			communicationService.logOperation(operation, payload, errorResponse,
					ErpCommunicationStatus.ERROR, null, ex.getMessage(), start, LocalDateTime.now());
			throw ex;
		}
	}
	
	/**
	 * Extract error response body from exception if it's an HTTP exception
	 */
	private Object extractErrorResponseBody(Exception ex) {
		// Check if the exception itself is an HttpStatusCodeException
		if (ex instanceof HttpStatusCodeException) {
			String responseBody = ((HttpStatusCodeException) ex).getResponseBodyAsString();
			if (responseBody != null && !responseBody.trim().isEmpty()) {
				return responseBody;
			}
		}
		
		// Check if the cause is an HttpStatusCodeException (common when wrapped in RuntimeException)
		Throwable cause = ex.getCause();
		if (cause instanceof HttpStatusCodeException) {
			String responseBody = ((HttpStatusCodeException) cause).getResponseBodyAsString();
			if (responseBody != null && !responseBody.trim().isEmpty()) {
				return responseBody;
			}
		}
		
		// No error response body available
		return null;
	}

	@FunctionalInterface
	private interface OperationExecutor<T> {
		T execute();
	}
}

