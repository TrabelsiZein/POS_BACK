package com.digithink.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public application configuration for the frontend (e.g. dual mode: ERP vs Standalone).
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
}
