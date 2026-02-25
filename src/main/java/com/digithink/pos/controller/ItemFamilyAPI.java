package com.digithink.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.config.ApplicationModeService;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.service.ItemFamilyService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("item-family")
@Log4j2
public class ItemFamilyAPI extends _BaseController<ItemFamily, Long, ItemFamilyService> {

	private final ApplicationModeService applicationModeService;

	public ItemFamilyAPI(ApplicationModeService applicationModeService) {
		this.applicationModeService = applicationModeService;
	}

	/**
	 * Create family. Only allowed in standalone mode (in ERP mode families come from sync).
	 */
	@PostMapping
	public ResponseEntity<?> create(@RequestBody ItemFamily entity) {
		if (!applicationModeService.isStandalone()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Item family creation is only available in standalone mode. In ERP mode families are synchronized from the ERP."));
		}
		try {
			log.info("ItemFamilyAPI::create");
			ItemFamily created = service.save(entity);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (Exception e) {
			log.error("ItemFamilyAPI::create:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}


