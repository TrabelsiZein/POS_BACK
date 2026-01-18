package com.digithink.pos.service;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.digithink.pos.model.SalesPrice;
import com.digithink.pos.repository.SalesPriceRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesPriceService extends _BaseService<SalesPrice, Long> {

	@Autowired
	private SalesPriceRepository salesPriceRepository;

	@Override
	protected _BaseRepository<SalesPrice, Long> getRepository() {
		return salesPriceRepository;
	}

	/**
	 * Get SalesPriceRepository specifically (for accessing in controller)
	 */
	public SalesPriceRepository getSalesPriceRepository() {
		return salesPriceRepository;
	}

	/**
	 * Get paginated sales prices with optional search criteria
	 */
	public Page<SalesPrice> findAllPaginated(int page, int size, String searchTerm) {
		Pageable pageable = PageRequest.of(page, size);
		
		if (StringUtils.hasText(searchTerm)) {
			Specification<SalesPrice> spec = buildSearchSpecification(searchTerm);
			return salesPriceRepository.findAll(spec, pageable);
		}
		
		return salesPriceRepository.findAll(pageable);
	}

	/**
	 * Build JPA Specification for search across multiple fields
	 */
	private Specification<SalesPrice> buildSearchSpecification(String searchTerm) {
		return (root, query, criteriaBuilder) -> {
			String searchPattern = "%" + searchTerm.toLowerCase() + "%";
			
			Predicate itemNoPredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("itemNo")), searchPattern);
			Predicate salesCodePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("salesCode")), searchPattern);
			Predicate salesTypePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("salesType")), searchPattern);
			Predicate responsibilityCenterPredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("responsibilityCenter")), searchPattern);
			Predicate currencyCodePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("currencyCode")), searchPattern);
			Predicate variantCodePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("variantCode")), searchPattern);
			Predicate unitOfMeasureCodePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("unitOfMeasureCode")), searchPattern);
			Predicate startingDatePredicate = criteriaBuilder.like(
				criteriaBuilder.lower(root.get("startingDate")), searchPattern);
			
			// For numeric fields, try to parse and match
			Predicate minimumQuantityPredicate = null;
			try {
				Double minQty = Double.parseDouble(searchTerm);
				minimumQuantityPredicate = criteriaBuilder.equal(root.get("minimumQuantity"), minQty);
			} catch (NumberFormatException e) {
				// Ignore if not a number
			}
			
			Predicate unitPricePredicate = null;
			try {
				Double price = Double.parseDouble(searchTerm);
				unitPricePredicate = criteriaBuilder.equal(root.get("unitPrice"), price);
			} catch (NumberFormatException e) {
				// Ignore if not a number
			}
			
			// Combine all predicates with OR
			Predicate combinedPredicate = criteriaBuilder.or(
				itemNoPredicate,
				salesCodePredicate,
				salesTypePredicate,
				responsibilityCenterPredicate,
				currencyCodePredicate,
				variantCodePredicate,
				unitOfMeasureCodePredicate,
				startingDatePredicate
			);
			
			// Add numeric predicates if they exist
			if (minimumQuantityPredicate != null) {
				combinedPredicate = criteriaBuilder.or(combinedPredicate, minimumQuantityPredicate);
			}
			if (unitPricePredicate != null) {
				combinedPredicate = criteriaBuilder.or(combinedPredicate, unitPricePredicate);
			}
			
			return combinedPredicate;
		};
	}
}

