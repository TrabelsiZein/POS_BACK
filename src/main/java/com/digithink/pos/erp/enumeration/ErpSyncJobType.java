package com.digithink.pos.erp.enumeration;

/**
 * Job types representing scheduled ERP synchronization routines.
 */
public enum ErpSyncJobType {
	IMPORT_ITEM_FAMILIES,
	IMPORT_ITEM_SUBFAMILIES,
	IMPORT_ITEMS,
	IMPORT_ITEM_BARCODES,
	IMPORT_LOCATIONS,
	IMPORT_CUSTOMERS,
	EXPORT_CUSTOMERS,
	EXPORT_TICKETS
}

