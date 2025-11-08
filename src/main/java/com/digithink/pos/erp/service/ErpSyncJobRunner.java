package com.digithink.pos.erp.service;

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

	public void run(ErpSyncJob job) {
		ErpSyncJobType jobType = job.getJobType();
		if (jobType == null) {
			LOGGER.warn("ERP sync job {} has no type defined", job.getJobType());
			return;
		}

		ErpSyncFilter filter = new ErpSyncFilter();
		switch (jobType) {
		case IMPORT_ITEM_FAMILIES:
			synchronizationManager.pullItemFamilies(filter);
			break;
		case IMPORT_ITEM_SUBFAMILIES:
			synchronizationManager.pullItemSubFamilies(filter);
			break;
		case IMPORT_ITEMS:
			synchronizationManager.pullItems(filter);
			break;
		case IMPORT_ITEM_BARCODES:
			synchronizationManager.pullItemBarcodes(filter);
			break;
		case IMPORT_LOCATIONS:
			synchronizationManager.pullLocations(filter);
			break;
		case IMPORT_CUSTOMERS:
			synchronizationManager.pullCustomers(filter);
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
