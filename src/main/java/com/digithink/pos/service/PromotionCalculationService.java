package com.digithink.pos.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.digithink.pos.dto.CartCalculateResponseDTO;
import com.digithink.pos.dto.PriceCalculateResponseDTO;
import com.digithink.pos.dto.PricingResult;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.Item;
import com.digithink.pos.model.Promotion;
import com.digithink.pos.model.enumeration.PromotionBenefitType;
import com.digithink.pos.model.enumeration.PromotionScope;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.PromotionRepository;

import lombok.extern.log4j.Log4j2;

/**
 * Core POS calculation engine.
 *
 * Precedence per item (mutually exclusive for item level):
 *   1. SalesPrice  → use it, done
 *   2. SalesDiscount (no SalesPrice) → use it, done
 *   3. Promotion   (no SalesPrice, no SalesDiscount) → find best match
 *
 * Cart promotions (CART scope) are independent and always considered.
 *
 * Promotion selection:
 *   - Scope priority: ITEM > ITEM_SUBFAMILY > ITEM_FAMILY (most specific wins)
 *   - Within scope: highest priority wins (ABSOLUTE — no best-value override)
 *   - requiresCode promotions: only active when their code is in appliedCodes
 *   - Filters: active + date valid + time valid + day valid + minimum quantity met
 */
@Service
@Log4j2
public class PromotionCalculationService {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PricingService pricingService;

	@Autowired
	private PromotionRepository promotionRepository;

	// ─── Item Price Calculation ──────────────────────────────────────────────────

	/**
	 * Calculate the final price for a single item in the POS cart.
	 *
	 * @param itemId       ID of the item
	 * @param quantity     Quantity in the cart (used for threshold promotions)
	 * @param customerId   ID of the selected customer (may be null → default customer)
	 * @param appliedCodes List of promo codes entered by the cashier
	 * @return Full pricing breakdown
	 */
	public PriceCalculateResponseDTO calculateItemPrice(Long itemId, Integer quantity,
			Long customerId, List<String> appliedCodes) {

		// Load item
		Item item = itemRepository.findById(itemId)
				.orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

		// Load customer
		Customer customer = resolveCustomer(customerId);

		int qty = (quantity != null && quantity > 0) ? quantity : 1;
		List<String> codes = (appliedCodes != null) ? appliedCodes : new ArrayList<>();

		// Delegate SalesPrice / SalesDiscount to existing PricingService
		PricingResult pricingResult = pricingService.calculateItemPrice(item, customer, qty, null);

		PriceCalculateResponseDTO response = new PriceCalculateResponseDTO();
		response.setItemId(itemId);
		response.setPriceIncludesVat(pricingResult.getPriceIncludesVat());

		// Step 2: SalesPrice found → use it, skip promotions entirely
		if ("SALES_PRICE".equals(pricingResult.getSource())) {
			response.setUnitPrice(pricingResult.getUnitPrice());
			response.setDiscountPercentage(pricingResult.getDiscountPercentage());
			response.setSource("SALES_PRICE");
			log.debug("calculateItemPrice: item={} → SALES_PRICE price={}", itemId, pricingResult.getUnitPrice());
			return response;
		}

		// Step 3: SalesDiscount found (no SalesPrice) → apply discount, skip promotions
		if (pricingResult.getDiscountPercentage() != null && pricingResult.getDiscountPercentage() > 0) {
			response.setUnitPrice(pricingResult.getUnitPrice());
			response.setDiscountPercentage(pricingResult.getDiscountPercentage());
			response.setSource("SALES_DISCOUNT");
			log.debug("calculateItemPrice: item={} → SALES_DISCOUNT {}%", itemId, pricingResult.getDiscountPercentage());
			return response;
		}

		// Step 4: No SalesPrice/SalesDiscount → find best promotion
		Double basePrice = item.getUnitPrice() != null ? item.getUnitPrice() : 0.0;
		Promotion bestPromotion = findBestItemPromotion(item, qty, codes);

		if (bestPromotion == null) {
			response.setUnitPrice(basePrice);
			response.setSource("BASE_PRICE");
			log.debug("calculateItemPrice: item={} → BASE_PRICE={}", itemId, basePrice);
			return response;
		}

		// Step 5: Apply promotion benefit
		applyItemPromotionBenefit(response, bestPromotion, basePrice);
		response.setPromotionId(bestPromotion.getId());
		response.setPromotionName(bestPromotion.getName());
		response.setPromotionCode(bestPromotion.getCode());
		response.setPromotionType(bestPromotion.getPromotionType().name());
		response.setSource("PROMOTION");

		log.debug("calculateItemPrice: item={} → PROMOTION '{}' ({})", itemId,
				bestPromotion.getName(), bestPromotion.getBenefitType());

		return response;
	}

	// ─── Cart Promotion Calculation ──────────────────────────────────────────────

	/**
	 * Calculate the best CART-scope promotion for the current cart.
	 * Called when the cashier clicks "Proceed to Payment" or enters a CART-scoped promo code.
	 *
	 * @param cartTotal    Cart total TTC (after all line-level discounts)
	 * @param appliedCodes List of promo codes entered by the cashier
	 * @return Cart discount result (cartDiscountAmount=0 if no promotion matched)
	 */
	public CartCalculateResponseDTO calculateCartPromotion(Double cartTotal, List<String> appliedCodes) {
		CartCalculateResponseDTO response = new CartCalculateResponseDTO();
		response.setCartDiscountAmount(0.0);
		response.setFinalTotal(cartTotal != null ? cartTotal : 0.0);

		if (cartTotal == null || cartTotal <= 0) {
			return response;
		}

		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		DayOfWeek todayDay = today.getDayOfWeek();
		List<String> codes = (appliedCodes != null) ? appliedCodes : new ArrayList<>();

		// Find all active CART-scope promotions valid today
		List<Promotion> cartPromos = promotionRepository.findActiveByScope(PromotionScope.CART, today);

		// Select best (highest priority; tiebreaker: highest discount value)
		Optional<Promotion> best = cartPromos.stream()
				.filter(p -> passesCodeFilter(p, codes))
				.filter(p -> passesAmountFilter(p, cartTotal))
				.filter(p -> passesTimeFilter(p, now))
				.filter(p -> passesDayFilter(p, todayDay))
				.max(Comparator
						.comparingInt((Promotion p) -> p.getPriority() != null ? p.getPriority() : 0)
						.thenComparingDouble(p -> p.getDiscountPercentage() != null ? p.getDiscountPercentage() : 0.0)
						.thenComparingDouble(p -> p.getDiscountAmount() != null ? p.getDiscountAmount() : 0.0));

		if (!best.isPresent()) {
			return response;
		}

		Promotion promo = best.get();
		double discount = 0.0;
		Double discountPct = null;

		if (promo.getBenefitType() == PromotionBenefitType.PERCENTAGE_DISCOUNT
				&& promo.getDiscountPercentage() != null) {
			discountPct = promo.getDiscountPercentage();
			discount = cartTotal * promo.getDiscountPercentage() / 100.0;
		} else if (promo.getBenefitType() == PromotionBenefitType.FIXED_DISCOUNT
				&& promo.getDiscountAmount() != null) {
			discount = Math.min(promo.getDiscountAmount(), cartTotal);
		}

		response.setPromotionId(promo.getId());
		response.setPromotionName(promo.getName());
		response.setPromotionCode(promo.getCode());
		response.setDiscountPercentage(discountPct);
		response.setCartDiscountAmount(discount);
		response.setFinalTotal(cartTotal - discount);

		log.debug("calculateCartPromotion: cartTotal={} → CART_PROMOTION '{}' discount={}", cartTotal,
				promo.getName(), discount);

		return response;
	}

	// ─── Promo Code Validation ───────────────────────────────────────────────────

	/**
	 * Validate a promo code entered by the cashier.
	 * Returns metadata so the frontend knows which cart items to re-calculate.
	 *
	 * @param code The code entered by the cashier
	 * @return Map with valid=true/false and promotion details when valid
	 */
	public Map<String, Object> validatePromoCode(String code) {
		Map<String, Object> result = new HashMap<>();

		if (!StringUtils.hasText(code)) {
			result.put("valid", false);
			result.put("reason", "EMPTY_CODE");
			return result;
		}

		Optional<Promotion> opt = promotionRepository.findByCode(code.trim());
		if (!opt.isPresent()) {
			result.put("valid", false);
			result.put("reason", "NOT_FOUND");
			return result;
		}

		Promotion p = opt.get();
		LocalDate today = LocalDate.now();

		// Must be active
		if (!Boolean.TRUE.equals(p.getActive())) {
			result.put("valid", false);
			result.put("reason", "INACTIVE");
			return result;
		}

		// Must require a code (auto promotions cannot be entered as promo codes)
		if (!Boolean.TRUE.equals(p.getRequiresCode())) {
			result.put("valid", false);
			result.put("reason", "NOT_A_PROMO_CODE");
			return result;
		}

		// Date validity
		if (p.getStartDate() != null && today.isBefore(p.getStartDate())) {
			result.put("valid", false);
			result.put("reason", "NOT_STARTED");
			return result;
		}
		if (p.getEndDate() != null && today.isAfter(p.getEndDate())) {
			result.put("valid", false);
			result.put("reason", "EXPIRED");
			return result;
		}

		// Valid — return metadata so frontend can decide which items to re-calculate
		result.put("valid", true);
		result.put("promotionId", p.getId());
		result.put("promotionName", p.getName());
		result.put("promotionType", p.getPromotionType().name());
		result.put("scope", p.getScope().name());
		result.put("benefitType", p.getBenefitType().name());
		result.put("itemId", p.getItem() != null ? p.getItem().getId() : null);
		result.put("itemFamilyId", p.getItemFamily() != null ? p.getItemFamily().getId() : null);
		result.put("itemSubFamilyId", p.getItemSubFamily() != null ? p.getItemSubFamily().getId() : null);

		return result;
	}

	// ─── Private Helpers ────────────────────────────────────────────────────────

	/**
	 * Find the best promotion for a single item.
	 * Scope priority: ITEM > ITEM_SUBFAMILY > ITEM_FAMILY (most specific wins).
	 * Within scope: highest priority wins (ABSOLUTE).
	 */
	private Promotion findBestItemPromotion(Item item, int quantity, List<String> codes) {
		LocalDate today = LocalDate.now();
		LocalTime now = LocalTime.now();
		DayOfWeek todayDay = today.getDayOfWeek();

		// ITEM scope (most specific)
		if (item.getId() != null) {
			Promotion best = selectBestFromList(
					promotionRepository.findActiveByItemId(item.getId(), today),
					quantity, codes, now, todayDay);
			if (best != null) return best;
		}

		// ITEM_SUBFAMILY scope
		if (item.getItemSubFamily() != null) {
			Promotion best = selectBestFromList(
					promotionRepository.findActiveByItemSubFamilyId(item.getItemSubFamily().getId(), today),
					quantity, codes, now, todayDay);
			if (best != null) return best;
		}

		// ITEM_FAMILY scope (least specific)
		if (item.getItemFamily() != null) {
			Promotion best = selectBestFromList(
					promotionRepository.findActiveByItemFamilyId(item.getItemFamily().getId(), today),
					quantity, codes, now, todayDay);
			if (best != null) return best;
		}

		return null;
	}

	/**
	 * From a list of pre-filtered (active + date-valid) promotions for a given scope,
	 * apply remaining filters and return the highest-priority match.
	 * Tiebreaker when priorities are equal: highest discount value wins (percentage → amount → freeQuantity).
	 */
	private Promotion selectBestFromList(List<Promotion> candidates, int quantity,
			List<String> codes, LocalTime now, DayOfWeek todayDay) {
		return candidates.stream()
				.filter(p -> passesCodeFilter(p, codes))
				.filter(p -> passesQuantityFilter(p, quantity))
				.filter(p -> passesTimeFilter(p, now))
				.filter(p -> passesDayFilter(p, todayDay))
				.max(Comparator
						.comparingInt((Promotion p) -> p.getPriority() != null ? p.getPriority() : 0)
						.thenComparingDouble(p -> p.getDiscountPercentage() != null ? p.getDiscountPercentage() : 0.0)
						.thenComparingDouble(p -> p.getDiscountAmount() != null ? p.getDiscountAmount() : 0.0)
						.thenComparingInt(p -> p.getFreeQuantity() != null ? p.getFreeQuantity() : 0))
				.orElse(null);
	}

	/** Auto promotions always pass. Code-required ones need their code in appliedCodes. */
	private boolean passesCodeFilter(Promotion p, List<String> codes) {
		if (!Boolean.TRUE.equals(p.getRequiresCode())) return true;
		return codes != null && codes.stream().anyMatch(c -> c.equalsIgnoreCase(p.getCode()));
	}

	/** If minimumQuantity is set, the cart quantity must meet or exceed it. */
	private boolean passesQuantityFilter(Promotion p, int quantity) {
		return p.getMinimumQuantity() == null || quantity >= p.getMinimumQuantity();
	}

	/** If minimumAmount is set, the cart total must meet or exceed it. */
	private boolean passesAmountFilter(Promotion p, Double cartTotal) {
		return p.getMinimumAmount() == null
				|| (cartTotal != null && cartTotal >= p.getMinimumAmount());
	}

	/** If timeStart/timeEnd are set, current time must be within the window. */
	private boolean passesTimeFilter(Promotion p, LocalTime now) {
		if (p.getTimeStart() == null && p.getTimeEnd() == null) return true;
		if (p.getTimeStart() != null && now.isBefore(p.getTimeStart())) return false;
		if (p.getTimeEnd() != null && now.isAfter(p.getTimeEnd())) return false;
		return true;
	}

	/** If dayOfWeek is set, today's day must be in the comma-separated list. */
	private boolean passesDayFilter(Promotion p, DayOfWeek todayDay) {
		if (!StringUtils.hasText(p.getDayOfWeek())) return true;
		return Arrays.stream(p.getDayOfWeek().split(","))
				.map(String::trim)
				.anyMatch(d -> d.equalsIgnoreCase(todayDay.name()));
	}

	/** Set the appropriate discount field on the response based on the promotion's benefit type. */
	private void applyItemPromotionBenefit(PriceCalculateResponseDTO response, Promotion p, double basePrice) {
		response.setUnitPrice(basePrice);
		switch (p.getBenefitType()) {
			case PERCENTAGE_DISCOUNT:
				response.setDiscountPercentage(p.getDiscountPercentage());
				break;
			case FIXED_DISCOUNT:
				response.setDiscountAmount(p.getDiscountAmount());
				break;
			case FREE_QUANTITY:
				response.setFreeQuantity(p.getFreeQuantity());
				break;
		}
	}

	/** Load customer by ID; fall back to default customer if not found. */
	private Customer resolveCustomer(Long customerId) {
		if (customerId != null) {
			Optional<Customer> customer = customerRepository.findById(customerId);
			if (customer.isPresent()) return customer.get();
		}
		return customerService.getDefaultCustomer();
	}
}
