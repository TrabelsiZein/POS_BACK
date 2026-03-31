package com.digithink.pos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.digithink.pos.model.Promotion;
import com.digithink.pos.model.enumeration.PromotionBenefitType;
import com.digithink.pos.model.enumeration.PromotionScope;
import com.digithink.pos.model.enumeration.PromotionType;
import com.digithink.pos.repository.PromotionRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PromotionService extends _BaseService<Promotion, Long> {

	@Autowired
	private PromotionRepository promotionRepository;

	@Override
	protected _BaseRepository<Promotion, Long> getRepository() {
		return promotionRepository;
	}

	public PromotionRepository getPromotionRepository() {
		return promotionRepository;
	}

	/** Returns total number of sales lines + headers that reference this promotion. */
	public long getUsageCount(Long promotionId) {
		return promotionRepository.countUsages(promotionId);
	}

	/**
	 * Validates that an update does not change locked fields when the promotion
	 * has already been used in sales. Throws IllegalStateException if violations found.
	 *
	 * Locked fields (when used): promotionType, benefitType, discountPercentage,
	 * discountAmount, freeQuantity, scope, item, itemFamily, itemSubFamily,
	 * minimumQuantity, minimumAmount, requiresCode, code.
	 *
	 * Allowed fields (always): name, description, startDate, endDate, active,
	 * priority, timeStart, timeEnd, dayOfWeek.
	 */
	public void validateUpdateAllowed(Long promotionId, Promotion updated) {
		Promotion existing = findById(promotionId)
				.orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + promotionId));

		long usageCount = promotionRepository.countUsages(promotionId);
		if (usageCount == 0) return; // Never used — full edit allowed

		List<String> violations = new ArrayList<>();

		if (!Objects.equals(existing.getPromotionType(), updated.getPromotionType()))
			violations.add("promotionType");
		if (!Objects.equals(existing.getBenefitType(), updated.getBenefitType()))
			violations.add("benefitType");
		if (!Objects.equals(existing.getDiscountPercentage(), updated.getDiscountPercentage()))
			violations.add("discountPercentage");
		if (!Objects.equals(existing.getDiscountAmount(), updated.getDiscountAmount()))
			violations.add("discountAmount");
		if (!Objects.equals(existing.getFreeQuantity(), updated.getFreeQuantity()))
			violations.add("freeQuantity");
		if (!Objects.equals(existing.getScope(), updated.getScope()))
			violations.add("scope");
		if (!entityIdEqual(existing.getItem(), updated.getItem()))
			violations.add("item");
		if (!entityIdEqual(existing.getItemFamily(), updated.getItemFamily()))
			violations.add("itemFamily");
		if (!entityIdEqual(existing.getItemSubFamily(), updated.getItemSubFamily()))
			violations.add("itemSubFamily");
		if (!Objects.equals(existing.getMinimumQuantity(), updated.getMinimumQuantity()))
			violations.add("minimumQuantity");
		if (!Objects.equals(existing.getMinimumAmount(), updated.getMinimumAmount()))
			violations.add("minimumAmount");
		if (!Objects.equals(existing.getRequiresCode(), updated.getRequiresCode()))
			violations.add("requiresCode");
		if (!Objects.equals(existing.getCode(), updated.getCode()))
			violations.add("code");

		if (!violations.isEmpty()) {
			throw new IllegalStateException(
				"Promotion has been used in " + usageCount + " sale(s). Cannot modify: " + violations);
		}
	}

	private boolean entityIdEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		try {
			Object idA = a.getClass().getMethod("getId").invoke(a);
			Object idB = b.getClass().getMethod("getId").invoke(b);
			return Objects.equals(idA, idB);
		} catch (Exception e) {
			return Objects.equals(a, b);
		}
	}

	/**
	 * Get paginated promotions with optional full-text search.
	 * Results are ordered by priority DESC, then createdAt DESC.
	 */
	public Page<Promotion> findAllPaginated(int page, int size, String searchTerm) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "priority").and(Sort.by(Sort.Direction.DESC, "createdAt")));

		if (StringUtils.hasText(searchTerm)) {
			Specification<Promotion> spec = buildSearchSpecification(searchTerm);
			return promotionRepository.findAll(spec, pageable);
		}

		return promotionRepository.findAll(pageable);
	}

	/**
	 * Build search specification: searches across code, name, description,
	 * promotionType (enum match), scope (enum match), benefitType (enum match), and numeric fields.
	 */
	private Specification<Promotion> buildSearchSpecification(String searchTerm) {
		return (root, query, cb) -> {
			String pattern = "%" + searchTerm.toLowerCase() + "%";

			// String LIKE predicates
			Predicate codePredicate        = cb.like(cb.lower(root.get("code")),        pattern);
			Predicate namePredicate        = cb.like(cb.lower(root.get("name")),        pattern);
			Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), pattern);

			// Enum predicates (exact match on enum name or displayName)
			Predicate promotionTypePredicate = null;
			PromotionType matchedType = PromotionType.fromString(searchTerm.trim());
			if (matchedType != null) {
				promotionTypePredicate = cb.equal(root.get("promotionType"), matchedType);
			}

			Predicate scopePredicate = null;
			PromotionScope matchedScope = PromotionScope.fromString(searchTerm.trim());
			if (matchedScope != null) {
				scopePredicate = cb.equal(root.get("scope"), matchedScope);
			}

			Predicate benefitTypePredicate = null;
			PromotionBenefitType matchedBenefit = PromotionBenefitType.fromString(searchTerm.trim());
			if (matchedBenefit != null) {
				benefitTypePredicate = cb.equal(root.get("benefitType"), matchedBenefit);
			}

			// Numeric predicates
			Predicate discountPctPredicate = null;
			Predicate discountAmtPredicate = null;
			Predicate minQtyPredicate      = null;
			try {
				Double numericValue = Double.parseDouble(searchTerm);
				discountPctPredicate = cb.equal(root.get("discountPercentage"), numericValue);
				discountAmtPredicate = cb.equal(root.get("discountAmount"),     numericValue);
				minQtyPredicate      = cb.equal(root.get("minimumQuantity"),    numericValue.intValue());
			} catch (NumberFormatException e) {
				// Not a number — skip numeric predicates
			}

			// Base OR across string fields
			Predicate combined = cb.or(codePredicate, namePredicate, descriptionPredicate);

			if (promotionTypePredicate != null) combined = cb.or(combined, promotionTypePredicate);
			if (scopePredicate         != null) combined = cb.or(combined, scopePredicate);
			if (benefitTypePredicate   != null) combined = cb.or(combined, benefitTypePredicate);
			if (discountPctPredicate   != null) combined = cb.or(combined, discountPctPredicate);
			if (discountAmtPredicate   != null) combined = cb.or(combined, discountAmtPredicate);
			if (minQtyPredicate        != null) combined = cb.or(combined, minQtyPredicate);

			return combined;
		};
	}
}
