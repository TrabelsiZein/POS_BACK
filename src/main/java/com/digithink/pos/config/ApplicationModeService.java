package com.digithink.pos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Exposes the application run mode: ERP (integrated with NAV/Business Central)
 * or Standalone (no ERP). All mode-dependent behaviour should use this service
 * so the single source of truth remains application.standalone.
 */
@Service
public class ApplicationModeService {

	@Value("${application.standalone:false}")
	private boolean standalone;

	/**
	 * True when the POS runs without an ERP (standalone mode).
	 * False when the POS is integrated with ERP (e.g. Dynamics NAV / Business Central).
	 */
	public boolean isStandalone() {
		return standalone;
	}

	/**
	 * True when the POS is integrated with an ERP (current mode for e.g. MTOP).
	 */
	public boolean isErpMode() {
		return !standalone;
	}
}
