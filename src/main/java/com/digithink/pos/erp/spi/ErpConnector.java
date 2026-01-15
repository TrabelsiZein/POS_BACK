package com.digithink.pos.erp.spi;

import java.util.List;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpSalesPriceDTO;
import com.digithink.pos.erp.dto.ErpSalesDiscountDTO;
import com.digithink.pos.erp.dto.ErpOperationResult;
import com.digithink.pos.erp.dto.ErpPaymentHeaderDTO;
import com.digithink.pos.erp.dto.ErpPaymentLineDTO;
import com.digithink.pos.erp.dto.ErpReturnDTO;
import com.digithink.pos.erp.dto.ErpReturnLineDTO;
import com.digithink.pos.erp.dto.ErpSessionDTO;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpTicketDTO;
import com.digithink.pos.erp.dto.ErpTicketLineDTO;
import com.digithink.pos.erp.dto.PullOperationResult;

/**
 * Contract to be implemented by ERP-specific connectors.
 */
public interface ErpConnector {

	List<ErpItemFamilyDTO> fetchItemFamilies(ErpSyncFilter filter);

	List<ErpItemSubFamilyDTO> fetchItemSubFamilies(ErpSyncFilter filter);

	List<ErpItemDTO> fetchItems(ErpSyncFilter filter);

	List<ErpItemBarcodeDTO> fetchItemBarcodes(ErpSyncFilter filter);

	List<ErpLocationDTO> fetchLocations(ErpSyncFilter filter);

	List<ErpCustomerDTO> fetchCustomers(ErpSyncFilter filter);

	List<ErpSalesPriceDTO> fetchSalesPrices(ErpSyncFilter filter);

	List<ErpSalesDiscountDTO> fetchSalesDiscounts(ErpSyncFilter filter);

	/**
	 * Get the result metadata from the last pull operation (for logging purposes).
	 * This should be called immediately after a fetch operation within the same thread.
	 * Returns null if no pull operation was performed or if metadata is not available.
	 */
	default PullOperationResult<?> getLastPullOperationResult() {
		return null;
	}

	/**
	 * Clear the stored pull operation result metadata (should be called after logging).
	 * Default implementation does nothing - connectors that store metadata should override this.
	 */
	default void clearLastPullOperationResult() {
		// Default: no-op
	}

	ErpOperationResult pushCustomer(ErpCustomerDTO customer);

	ErpOperationResult pushTicket(ErpTicketDTO ticket);

	/**
	 * Push ticket header to ERP and return external reference (document number)
	 */
	ErpOperationResult pushTicketHeader(ErpTicketDTO ticket);

	/**
	 * Push a single ticket line to ERP
	 */
	ErpOperationResult pushTicketLine(ErpTicketDTO ticket, String externalReference, ErpTicketLineDTO line);

	/**
	 * Update ticket status in ERP (e.g., POS_Order flag)
	 */
	ErpOperationResult updateTicketStatus(String externalReference, boolean posOrder);

	/**
	 * Push payment header to ERP and return external reference (document number)
	 */
	ErpOperationResult pushPaymentHeader(ErpPaymentHeaderDTO headerDTO);

	/**
	 * Push a single payment line to ERP
	 */
	ErpOperationResult pushPaymentLine(String paymentHeaderDocNo, ErpPaymentLineDTO lineDTO);

	/**
	 * Push return header to ERP and return external reference (document number)
	 * TODO: Implement with Dynamics NAV later
	 */
	default ErpOperationResult pushReturnHeader(ErpReturnDTO returnDTO) {
		// Stub implementation - to be implemented with NAV later
		return ErpOperationResult.failure("Return export not yet implemented");
	}

	/**
	 * Push a single return line to ERP TODO: Implement with Dynamics NAV later
	 */
	default ErpOperationResult pushReturnLine(ErpReturnDTO returnDTO, String externalReference,
			ErpReturnLineDTO lineDTO) {
		// Stub implementation - to be implemented with NAV later
		return ErpOperationResult.failure("Return line export not yet implemented");
	}

	/**
	 * Push cashier session to ERP and return external reference (document number)
	 */
	default ErpOperationResult pushSession(ErpSessionDTO sessionDTO) {
		// Stub implementation - to be implemented with NAV later
		return ErpOperationResult.failure("Session export not yet implemented");
	}
}
