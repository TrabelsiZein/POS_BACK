package com.digithink.pos.service;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.digithink.pos.model.SalesDiscount;
import com.digithink.pos.repository.SalesDiscountRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesDiscountService extends _BaseService<SalesDiscount, Long> {

	@Autowired
	private SalesDiscountRepository salesDiscountRepository;

	@Override
	protected _BaseRepository<SalesDiscount, Long> getRepository() {
		return salesDiscountRepository;
	}

	/**
	 * Get SalesDiscountRepository specifically (for accessing in controller)
	 */
	public SalesDiscountRepository getSalesDiscountRepository() {
		return salesDiscountRepository;
	}

	/**
	 * Get paginated sales discounts with optional search criteria
	 */
	public Page<SalesDiscount> findAllPaginated(int page, int size, String searchTerm) {
		Pageable pageable = PageRequest.of(page, size);
		
		if (StringUtils.hasText(searchTerm)) {
			Specification<SalesDiscount> spec = buildSearchSpecification(searchTerm);
			return salesDiscountRepository.findAll(spec, pageable);
		}
		
		return salesDiscountRepository.findAll(pageable);
	}

	/**
	 * Build JPA Specification for search across multiple fields
	 */
	private Specification<SalesDiscount> buildSearchSpecification(String searchTerm) {
		return (root, query, criteriaBuilder) -> {
			String searchPattern = "%" + searchTerm.toLowerCase() + "%";
			
			Predicate codePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("code")), searchPattern);
			Predicate salesCodePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("salesCode")), searchPattern);
			Predicate salesTypePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("salesType")), searchPattern);
			Predicate typePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("type")), searchPattern);
			
			// For numeric fields, try to parse and match
			Predicate lineDiscountPredicate = null;
			try {
				Double discount = Double.parseDouble(searchTerm);
				lineDiscountPredicate = criteriaBuilder.equal(root.get("lineDiscount"), discount);
			} catch (NumberFormatException e) {
				// Ignore if not a number
			}
			
			// Combine all predicates with OR
			Predicate combinedPredicate = criteriaBuilder.or(
				codePredicate,
				salesCodePredicate,
				salesTypePredicate,
				typePredicate
			);
			
			// Add numeric predicate if it exists
			if (lineDiscountPredicate != null) {
				combinedPredicate = criteriaBuilder.or(combinedPredicate, lineDiscountPredicate);
			}
			
			return combinedPredicate;
		};
	}
}

