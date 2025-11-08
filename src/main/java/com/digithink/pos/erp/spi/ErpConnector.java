package com.digithink.pos.erp.spi;

import java.util.List;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpOperationResult;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpTicketDTO;

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

	ErpOperationResult pushCustomer(ErpCustomerDTO customer);

	ErpOperationResult pushTicket(ErpTicketDTO ticket);
}

