package com.digithink.pos.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.config.ApplicationModeService;
import com.digithink.pos.dto.PricingResult;
import com.digithink.pos.dto.StandaloneQuickProductRequestDTO;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemBarcode;

import java.util.Optional;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.service.CustomerService;
import com.digithink.pos.service.ItemBarcodeService;
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

	@Autowired
	private ApplicationModeService applicationModeService;

	@Autowired
	private ItemBarcodeService itemBarcodeService;

	@Value("${pos.pricing.enable-sales-price-group:false}")
	private boolean priceGroupEnabled;

	/**
	 * Create a product with default family/subfamily and one barcode. Only allowed in standalone mode.
	 */
	@PostMapping("/standalone-quick-product")
	public ResponseEntity<?> createStandaloneQuickProduct(@RequestBody StandaloneQuickProductRequestDTO request) {
		if (!applicationModeService.isStandalone()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Product creation is only available in standalone mode. In ERP mode products are synchronized from the ERP."));
		}
		try {
			if (request.getName() == null || request.getName().trim().isEmpty()) {
				return ResponseEntity.badRequest().body(createErrorResponse("Product name is required"));
			}
			if (request.getUnitPrice() == null || request.getUnitPrice() < 0) {
				return ResponseEntity.badRequest().body(createErrorResponse("Unit price is required and must be >= 0"));
			}
			Item item = service.createStandaloneQuickProduct(
					request.getName(),
					request.getItemCode(),
					request.getUnitPrice());
			String barcodeValue = (request.getBarcode() != null && !request.getBarcode().trim().isEmpty())
					? request.getBarcode().trim()
					: item.getItemCode();
			ItemBarcode barcode = new ItemBarcode();
			barcode.setItem(item);
			barcode.setBarcode(barcodeValue);
			barcode.setIsPrimary(true);
			barcode.setActive(true);
			itemBarcodeService.save(barcode);
			Map<String, Object> response = new HashMap<>();
			response.put("item", item);
			response.put("barcode", barcodeValue);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
		} catch (Exception e) {
			log.error("ItemAPI::createStandaloneQuickProduct:error: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Create item. Only allowed in standalone mode.
	 */
	@Override
	@PostMapping
	public ResponseEntity<?> create(@RequestBody Item entity) {
		if (!applicationModeService.isStandalone()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Item creation is only available in standalone mode. In ERP mode items are synchronized from the ERP."));
		}
		try {
			log.info("ItemAPI::create");
			Item created = service.save(entity);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (Exception e) {
			log.error("ItemAPI::create:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Update item. Only allowed in standalone mode.
	 */
	@Override
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Item entity) {
		if (!applicationModeService.isStandalone()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Item update is only available in standalone mode. In ERP mode items are synchronized from the ERP."));
		}
		try {
			log.info("ItemAPI::update::" + id);
			Optional<Item> existing = service.findById(id);
			if (!existing.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			entity.setId(existing.get().getId());
			Item updated = service.save(entity);
			return ResponseEntity.ok(updated);
		} catch (Exception e) {
			log.error("ItemAPI::update:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

	/**
	 * Delete item. Only allowed in standalone mode.
	 */
	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable Long id) {
		if (!applicationModeService.isStandalone()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(createErrorResponse("Item deletion is only available in standalone mode. In ERP mode items are synchronized from the ERP."));
		}
		try {
			log.info("ItemAPI::deleteById::" + id);
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			log.error("ItemAPI::deleteById:error: " + getDetailedMessage(e), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(getDetailedMessage(e)));
		}
	}

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
