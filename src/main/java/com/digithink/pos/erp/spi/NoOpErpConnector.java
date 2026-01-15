package com.digithink.pos.erp.spi;

import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpSalesPriceDTO;
import com.digithink.pos.erp.dto.ErpSalesDiscountDTO;
import com.digithink.pos.erp.dto.ErpOperationResult;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpPaymentHeaderDTO;
import com.digithink.pos.erp.dto.ErpPaymentLineDTO;
import com.digithink.pos.erp.dto.ErpReturnDTO;
import com.digithink.pos.erp.dto.ErpReturnLineDTO;
import com.digithink.pos.erp.dto.ErpTicketDTO;
import com.digithink.pos.erp.dto.ErpTicketLineDTO;

/**
 * Default connector used when no ERP implementation is configured.
 */
@Component
@ConditionalOnMissingBean(ErpConnector.class)
public class NoOpErpConnector implements ErpConnector {

	@Override
	public List<ErpItemFamilyDTO> fetchItemFamilies(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpItemSubFamilyDTO> fetchItemSubFamilies(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpItemDTO> fetchItems(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpItemBarcodeDTO> fetchItemBarcodes(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpLocationDTO> fetchLocations(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpCustomerDTO> fetchCustomers(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpSalesPriceDTO> fetchSalesPrices(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public List<ErpSalesDiscountDTO> fetchSalesDiscounts(ErpSyncFilter filter) {
		return Collections.emptyList();
	}

	@Override
	public ErpOperationResult pushCustomer(ErpCustomerDTO customer) {
		return ErpOperationResult.failure("ERP connector not configured");
	}

	@Override
	public ErpOperationResult pushTicket(ErpTicketDTO ticket) {
		return ErpOperationResult.failure("ERP connector not configured");
	}

	@Override
	public ErpOperationResult pushTicketHeader(ErpTicketDTO ticket) {
		return ErpOperationResult.failure("ERP connector not configured");
	}

	@Override
	public ErpOperationResult pushTicketLine(ErpTicketDTO ticket, String externalReference, ErpTicketLineDTO line) {
		return ErpOperationResult.failure("ERP connector not configured");
	}

	@Override
	public ErpOperationResult updateTicketStatus(String externalReference, boolean posOrder) {
		return ErpOperationResult.failure("ERP connector not configured");
	}

	@Override
	public ErpOperationResult pushPaymentHeader(ErpPaymentHeaderDTO headerDTO) {
		return ErpOperationResult.success("NO-OP", null, null, null);
	}

	@Override
	public ErpOperationResult pushPaymentLine(String paymentHeaderDocNo, ErpPaymentLineDTO lineDTO) {
		return ErpOperationResult.success("NO-OP", null, null, null);
	}

	@Override
	public ErpOperationResult pushReturnHeader(ErpReturnDTO returnDTO) {
		return ErpOperationResult.failure("Return export not yet implemented");
	}

	@Override
	public ErpOperationResult pushReturnLine(ErpReturnDTO returnDTO, String externalReference,
			ErpReturnLineDTO lineDTO) {
		return ErpOperationResult.failure("Return line export not yet implemented");
	}
}

