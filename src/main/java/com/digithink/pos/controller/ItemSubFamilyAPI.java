package com.digithink.pos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.service.ItemFamilyService;
import com.digithink.pos.service.ItemSubFamilyService;

@RestController
@RequestMapping("item-sub-family")
public class ItemSubFamilyAPI extends _BaseController<ItemSubFamily, Long, ItemSubFamilyService> {

	@Autowired
	private ItemFamilyService itemFamilyService;

	@GetMapping("/by-family/{familyId}")
	public ResponseEntity<?> getByFamily(@PathVariable Long familyId) {
		try {
			ItemFamily family = itemFamilyService.findById(familyId)
					.orElseThrow(() -> new IllegalArgumentException("Item family not found: " + familyId));
			List<ItemSubFamily> subFamilies = service.findByFamily(family);
			return ResponseEntity.ok(subFamilies);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}


