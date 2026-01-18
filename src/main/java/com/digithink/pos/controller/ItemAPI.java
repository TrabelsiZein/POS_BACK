package com.digithink.pos.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.dto.PricingResult;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.Item;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.service.CustomerService;
import com.digithink.pos.service.ItemService;
import com.digithink.pos.service.PricingService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("item")
@Log4j2
public class ItemAPI extends _BaseController<Item, Long, ItemService> {

	@Autowired
	private PricingService pricingService;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Value("${pos.pricing.enable-sales-price-group:false}")
	private boolean priceGroupEnabled;

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

	/**
	 * Get pricing configuration (for frontend optimization)
	 * Frontend can check this to decide whether to call calculate-price API
	 */
	@GetMapping("/pricing-config")
	public ResponseEntity<?> getPricingConfig() {
		Map<String, Object> config = new HashMap<>();
		config.put("priceGroupEnabled", priceGroupEnabled);
		return ResponseEntity.ok(config);
	}

	/**
	 * Calculate price for an item (for POS cart) Returns calculated price,
	 * discount, and source
	 */
	@GetMapping("/{itemId}/calculate-price")
	public ResponseEntity<?> calculateItemPrice(@PathVariable Long itemId,
			@RequestParam(name = "customerId", required = false) Long customerId,
			@RequestParam(name = "quantity", defaultValue = "1") Integer quantity) {
		try {
			log.info("ItemAPI::calculateItemPrice: itemId={}, customerId={}, quantity={}", itemId, customerId,
					quantity);

			Item item = service.findById(itemId)
					.orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

			// Early exit: If pricing is disabled, return item price without fetching customer
			// This avoids unnecessary database calls when feature is disabled
			if (!priceGroupEnabled) {
				Map<String, Object> response = new HashMap<>();
				response.put("itemId", itemId);
				response.put("unitPrice", item.getUnitPrice() != null ? item.getUnitPrice() : 0.0);
				response.put("priceIncludesVat", false);
				response.put("discountPercentage", null);
				response.put("source", "ITEM");
				return ResponseEntity.ok(response);
			}

			// Pricing is enabled - fetch customer for price calculation
			Customer customer = null;
			if (customerId != null) {
				customer = customerRepository.findById(customerId).orElse(null);
			}
			// If no customer provided, use default customer from GeneralSetup
			if (customer == null) {
				customer = customerService.getDefaultCustomer();
			}

			PricingResult pricingResult = pricingService.calculateItemPrice(item, customer, quantity, null);

			Map<String, Object> response = new HashMap<>();
			response.put("itemId", itemId);
			response.put("unitPrice", pricingResult.getUnitPrice());
			response.put("priceIncludesVat", pricingResult.getPriceIncludesVat());
			response.put("discountPercentage", pricingResult.getDiscountPercentage());
			response.put("source", pricingResult.getSource());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("ItemAPI::calculateItemPrice:error: " + e.getMessage(), e);
			return ResponseEntity.status(500).body(createErrorResponse(getDetailedMessage(e)));
		}
	}
}
