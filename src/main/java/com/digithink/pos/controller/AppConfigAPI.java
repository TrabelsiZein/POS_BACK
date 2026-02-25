package com.digithink.pos.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.config.ApplicationModeService;
import com.digithink.pos.dto.AppConfigDTO;

import lombok.RequiredArgsConstructor;

/**
 * Exposes application configuration for the frontend (e.g. ERP vs Standalone mode).
 * Used to control UI visibility (ERP menu, sync columns) without changing backend behaviour.
 */
@RestController
@RequestMapping("config")
@RequiredArgsConstructor
public class AppConfigAPI {

	private final ApplicationModeService applicationModeService;

	@Value("${pos.pricing.enable-sales-price-group:false}")
	private boolean enableSalesPriceGroup;

	/**
	 * GET /config - returns public app config (standalone, enableSalesPriceGroup, etc.).
	 * Allowed without authentication so the frontend can load it on app init.
	 */
	@GetMapping
	public ResponseEntity<AppConfigDTO> getConfig() {
		return ResponseEntity.ok(new AppConfigDTO(applicationModeService.isStandalone(), enableSalesPriceGroup));
	}
}
