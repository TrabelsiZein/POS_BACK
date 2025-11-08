package com.digithink.pos.erp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

	public void run(ErpSyncJob job) {
		ErpSyncJobType jobType = job.getJobType();
		if (jobType == null) {
			LOGGER.warn("ERP sync job {} has no type defined", job.getJobType());
			return;
		}

		ErpSyncFilter filter = checkpointService.createFilterForJob(jobType);
		switch (jobType) {
		case IMPORT_ITEM_FAMILIES:
			List<?> families = synchronizationManager.pullItemFamilies(filter);
			checkpointService.updateLastSync(jobType, families);
			break;
		case IMPORT_ITEM_SUBFAMILIES:
			List<?> subFamilies = synchronizationManager.pullItemSubFamilies(filter);
			checkpointService.updateLastSync(jobType, subFamilies);
			break;
		case IMPORT_ITEMS:
			List<?> items = synchronizationManager.pullItems(filter);
			checkpointService.updateLastSync(jobType, items);
			break;
		case IMPORT_ITEM_BARCODES:
			List<?> barcodes = synchronizationManager.pullItemBarcodes(filter);
			checkpointService.updateLastSync(jobType, barcodes);
			break;
		case IMPORT_LOCATIONS:
			List<?> locations = synchronizationManager.pullLocations(filter);
			checkpointService.updateLastSync(jobType, locations);
			break;
		case IMPORT_CUSTOMERS:
			List<?> customers = synchronizationManager.pullCustomers(filter);
			checkpointService.updateLastSync(jobType, customers);
			break;
		case EXPORT_CUSTOMERS:
			LOGGER.info("ERP sync job {} is configured for EXPORT_CUSTOMERS. "
					+ "Data extraction from POS is not yet implemented.", job.getJobType());
			break;
		case EXPORT_TICKETS:
			LOGGER.info(
					"ERP sync job {} is configured for EXPORT_TICKETS. " + "Ticket extraction is not yet implemented.",
					job.getJobType());
			break;
		default:
			LOGGER.warn("Unhandled ERP sync job type {} for job {}", jobType, job.getJobType());
		}
	}
}
