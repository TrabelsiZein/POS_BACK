package com.digithink.pos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Item;
import com.digithink.pos.service.ItemService;

@RestController
@RequestMapping("item")
public class ItemAPI extends _BaseController<Item, Long, ItemService> {

	@GetMapping("/by-family/{familyId}")
	public ResponseEntity<?> getByFamily(@PathVariable Long familyId) {
		try {
			return ResponseEntity.ok(service.findActiveByFamilyId(familyId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	@GetMapping("/by-sub-family/{subFamilyId}")
	public ResponseEntity<?> getBySubFamily(@PathVariable Long subFamilyId) {
		try {
			return ResponseEntity.ok(service.findActiveBySubFamilyId(subFamilyId));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}
