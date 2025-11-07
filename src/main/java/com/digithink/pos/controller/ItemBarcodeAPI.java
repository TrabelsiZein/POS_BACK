package com.digithink.pos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemBarcode;
import com.digithink.pos.service.ItemBarcodeService;
import com.digithink.pos.service.ItemService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("item-barcode")
@Log4j2
public class ItemBarcodeAPI extends _BaseController<ItemBarcode, Long, ItemBarcodeService> {

	@Autowired
	private ItemBarcodeService itemBarcodeService;

	@Autowired
	private ItemService itemService;

	/**
	 * Get all barcodes for an item
	 */
	@GetMapping("/item/{itemId}")
	public ResponseEntity<?> getBarcodesByItemId(@PathVariable Long itemId) {
		try {
			log.info("ItemBarcodeAPI::getBarcodesByItemId: " + itemId);
			List<ItemBarcode> barcodes = itemBarcodeService.getBarcodesByItemId(itemId);
			return ResponseEntity.ok(barcodes);
		} catch (Exception e) {
			log.error("ItemBarcodeAPI::getBarcodesByItemId:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Find item by barcode (for POS scanning)
	 */
	@GetMapping("/barcode/{barcode}")
	public ResponseEntity<?> getItemByBarcode(@PathVariable String barcode) {
		try {
			log.info("ItemBarcodeAPI::getItemByBarcode: " + barcode);
			java.util.Optional<Item> item = itemBarcodeService.getItemByBarcode(barcode);
			if (item.isPresent()) {
				return ResponseEntity.ok(item.get());
			} else {
				return ResponseEntity.status(404).body(createErrorResponse("Item not found with barcode: " + barcode));
			}
		} catch (Exception e) {
			log.error("ItemBarcodeAPI::getItemByBarcode:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Get all items with their barcodes (for admin/responsible view)
	 */
	@GetMapping("/items-with-barcodes")
	public ResponseEntity<?> getAllItemsWithBarcodes() {
		try {
			log.info("ItemBarcodeAPI::getAllItemsWithBarcodes");
			List<Item> items = itemService.findAll();
			List<ItemBarcode> allBarcodes = itemBarcodeService.findAll();
			
			// Group barcodes by item ID
			Map<Long, List<ItemBarcode>> barcodesByItem = allBarcodes.stream()
				.filter(b -> b.getActive() != null && b.getActive())
				.collect(Collectors.groupingBy(b -> b.getItem().getId()));
			
			// Build response
			List<Map<String, Object>> result = items.stream()
				.filter(item -> item.getActive() != null && item.getActive())
				.map(item -> {
					Map<String, Object> itemMap = new HashMap<>();
					itemMap.put("item", item);
					itemMap.put("barcodes", barcodesByItem.getOrDefault(item.getId(), List.of()));
					return itemMap;
				})
				.collect(Collectors.toList());
			
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			log.error("ItemBarcodeAPI::getAllItemsWithBarcodes:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}

