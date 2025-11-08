package com.digithink.pos.erp.dynamicsnav.connector;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpOperationResult;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dto.ErpTicketDTO;
import com.digithink.pos.erp.dynamicsnav.client.DynamicsNavRestClient;
import com.digithink.pos.erp.dynamicsnav.mapper.DynamicsNavMapper;
import com.digithink.pos.erp.spi.ErpConnector;

import lombok.RequiredArgsConstructor;

@Component
@ConditionalOnProperty(prefix = "erp.dynamicsnav", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class DynamicsNavConnector implements ErpConnector {

	private final DynamicsNavRestClient restClient;
	private final DynamicsNavMapper mapper;

	@Override
	public List<ErpItemFamilyDTO> fetchItemFamilies(ErpSyncFilter filter) {
		return mapper.toItemFamilyDTOs(restClient.fetchItemFamilies());
	}

	@Override
	public List<ErpItemSubFamilyDTO> fetchItemSubFamilies(ErpSyncFilter filter) {
		return mapper.toItemSubFamilyDTOs(restClient.fetchItemSubFamilies());
	}

	@Override
	public List<ErpItemDTO> fetchItems(ErpSyncFilter filter) {
		return mapper.toItemDTOs(restClient.fetchItems(filter));
	}

	@Override
	public List<ErpItemBarcodeDTO> fetchItemBarcodes(ErpSyncFilter filter) {
		return mapper.toItemBarcodeDTOs(restClient.fetchItemBarcodes(filter));
	}

	@Override
	public List<ErpLocationDTO> fetchLocations(ErpSyncFilter filter) {
		return mapper.toLocationDTOs(restClient.fetchLocations());
	}

	@Override
	public List<ErpCustomerDTO> fetchCustomers(ErpSyncFilter filter) {
		return java.util.Collections.emptyList();
	}

	@Override
	public ErpOperationResult pushCustomer(ErpCustomerDTO customer) {
		return ErpOperationResult.failure("Not implemented");
	}

	@Override
	public ErpOperationResult pushTicket(ErpTicketDTO ticket) {
		return ErpOperationResult.failure("Not implemented");
	}
}
