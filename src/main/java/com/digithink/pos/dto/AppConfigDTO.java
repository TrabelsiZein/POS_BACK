package com.digithink.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public application configuration for the frontend (e.g. dual mode: ERP vs Standalone, franchise flags).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfigDTO {

	/**
	 * True when the POS runs in standalone mode (no ERP). False when integrated with ERP.
	 */
	private boolean standalone;

	/**
	 * True when sales price group and sales discount features are enabled (admin views and pricing logic).
	 */
	private boolean enableSalesPriceGroup;

	/**
	 * True when the loyalty (fidélité) program is enabled. Controlled by GeneralSetup LOYALTY_ENABLED.
	 */
	private boolean loyaltyEnabled;

	/**
	 * True when this instance is the central franchise admin (HQ).
	 * Enables franchise sync APIs and franchise-specific admin UI.
	 */
	private boolean franchiseAdmin;

	/**
	 * True when this instance is a franchise client.
	 * Disables item CRUD and manual purchases; enables sync UI from admin.
	 */
	private boolean franchiseCustomer;

	/**
	 * True when the franchise client is allowed to add/manage its own local items.
	 * Items synced from the franchise admin remain read-only regardless.
	 * Always false when not in franchise client mode.
	 */
	private boolean allowLocalItems;

	/**
	 * Current license status: VALID, WARNING, EXPIRED, or MISSING.
	 * Frontend uses this to show warning banners and block access when needed.
	 */
	private String licenseStatus;

	/**
	 * Number of days until the current license expires.
	 * Negative when license is expired or missing. Used for warning countdown.
	 */
	private long licenseDaysUntilExpiry;
}
