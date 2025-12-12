package com.digithink.pos.erp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.enumeration.ErpSyncJobType;
import com.digithink.pos.erp.model.ErpSyncJob;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpSyncJobRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpSyncJobRunner.class);

	private final ErpSynchronizationManager synchronizationManager;
	private final ErpSyncCheckpointService checkpointService;
	private final ErpItemBootstrapService erpItemBootstrapService;
	private final TicketExportService ticketExportService;
	private final ReturnExportService returnExportService;
	private final SessionExportService sessionExportService;

	public void run(ErpSyncJob job) {
		ErpSyncJobType jobType = job.getJobType();
		if (jobType == null) {
			LOGGER.warn("ERP sync job {} has no type defined", job.getJobType());
			return;
		}

		ErpSyncFilter filter = checkpointService.createFilterForJob(jobType);
		try {
			switch (jobType) {
			case IMPORT_ITEM_FAMILIES:
				List<ErpItemFamilyDTO> families = synchronizationManager.pullItemFamilies(filter);
				erpItemBootstrapService.importItemFamilies(families);
				checkpointService.updateLastSync(jobType, families);
				break;
			case IMPORT_ITEM_SUBFAMILIES:
				List<ErpItemSubFamilyDTO> subFamilies = synchronizationManager.pullItemSubFamilies(filter);
				erpItemBootstrapService.importItemSubFamilies(subFamilies);
				checkpointService.updateLastSync(jobType, subFamilies);
				break;
			case IMPORT_ITEMS:
				List<ErpItemDTO> items = synchronizationManager.pullItems(filter);
				erpItemBootstrapService.importItems(items);
				checkpointService.updateLastSync(jobType, items);
				break;
			case IMPORT_ITEM_BARCODES:
				List<ErpItemBarcodeDTO> barcodes = synchronizationManager.pullItemBarcodes(filter);
				erpItemBootstrapService.importItemBarcodes(barcodes);
				checkpointService.updateLastSync(jobType, barcodes);
				break;
			case IMPORT_LOCATIONS:
				List<ErpLocationDTO> locations = synchronizationManager.pullLocations(filter);
				erpItemBootstrapService.importLocations(locations);
				checkpointService.updateLastSync(jobType, locations);
				break;
			case IMPORT_CUSTOMERS:
				List<ErpCustomerDTO> customers = synchronizationManager.pullCustomers(filter);
				erpItemBootstrapService.importCustomers(customers);
				checkpointService.updateLastSync(jobType, customers);
				break;
			case EXPORT_CUSTOMERS:
				LOGGER.info("ERP sync job {} is configured for EXPORT_CUSTOMERS. "
						+ "Data extraction from POS is not yet implemented.", job.getJobType());
				break;
			case EXPORT_TICKETS:
				ticketExportService.exportTickets();
				break;
			case EXPORT_RETURNS:
				returnExportService.exportReturns();
				break;
			case EXPORT_SESSIONS:
				sessionExportService.exportSessions();
				break;
			default:
				LOGGER.warn("Unhandled ERP sync job type {} for job {}", jobType, job.getJobType());
			}
		} catch (ErpSyncWarningException warning) {
			LOGGER.warn("ERP sync job {} skipped: {}", jobType, warning.getMessage());
			throw warning;
		}
	}
}
