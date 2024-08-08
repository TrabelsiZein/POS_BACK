package com.digithink.business_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.security.CurrentUserProvider;

public abstract class _BaseService<T extends _BaseEntity, ID> implements __BaseService<T, ID> {

	@Autowired
	protected CurrentUserProvider currentUserProvider;

	protected abstract _BaseRepository<T, ID> getRepository();

	@Override
	public List<T> findAll() {
		return getRepository().findAllByCompanyOrderByUpdatedAtDesc(currentUserProvider.getCurrentCompanyId());
	}

	@Override
	public Optional<T> findById(ID id) {
		return getRepository().findById(id);
	}

//	@Override
//	public List<T> findByField(String fieldName, String operation, Object value) {
//		@SuppressWarnings({ "unchecked", "rawtypes" })
//		Specification<T> specification = (root, query, criteriaBuilder) -> {
//			Predicate predicate = null;
//			switch (operation) {
//			case "=":
//				predicate = criteriaBuilder.equal(root.get(fieldName), value);
//				break;
//			case ">":
//				predicate = criteriaBuilder.greaterThan(root.get(fieldName), (Comparable) value);
//				break;
//			case "<":
//				predicate = criteriaBuilder.lessThan(root.get(fieldName), (Comparable) value);
//				break;
//			default:
//				throw new IllegalArgumentException("Invalid operation: " + operation);
//			}
//			return predicate;
//		};
//		return getRepository().findAll(specification);
//	}

	@Override
	public List<T> findByField(String fieldName, String operation, Object value) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Specification<T> fieldSpecification = (root, query, criteriaBuilder) -> {
			Predicate predicate;
			switch (operation) {
			case "=":
				predicate = criteriaBuilder.equal(root.get(fieldName), value);
				break;
			case ">":
				predicate = criteriaBuilder.greaterThan(root.get(fieldName), (Comparable) value);
				break;
			case "<":
				predicate = criteriaBuilder.lessThan(root.get(fieldName), (Comparable) value);
				break;
			default:
				throw new IllegalArgumentException("Invalid operation: " + operation);
			}
			return predicate;
		};

		Specification<T> companySpecification = (root, query, criteriaBuilder) -> criteriaBuilder
				.equal(root.get("company"), currentUserProvider.getCurrentCompanyId());

		Specification<T> combinedSpecification = Specification.where(fieldSpecification).and(companySpecification);

		return getRepository().findAll(combinedSpecification, Sort.by(Sort.Direction.DESC, "updatedAt"));
	}

	@Override
	@Transactional
	public T save(T entity) {
		String username = currentUserProvider.getCurrentUserName();

		if (entity.getId() == null) {
			entity.setCreatedBy(username);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setCompany(currentUserProvider.getCurrentCompanyId());
		} else {
			entity.setUpdatedBy(username);
			entity.setUpdatedAt(LocalDateTime.now());
		}

		return getRepository().save(entity);
	}

	public void deleteById(ID id) {
		getRepository().deleteById(id);
	}

}
