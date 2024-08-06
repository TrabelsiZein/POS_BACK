package com.digithink.business_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.security.CurrentUserProvider;

public abstract class _BaseService<T extends _BaseEntity, ID> {

	@Autowired
	protected CurrentUserProvider currentUserProvider;

	protected abstract JpaRepository<T, ID> getRepository();

	protected abstract JpaSpecificationExecutor<T> getJpaSpecificationExecutor();

	public List<T> findAll() {
		return getRepository().findAll();
	}

	public Optional<T> findById(ID id) {
		return getRepository().findById(id);
	}

	public List<T> findByField(String fieldName, String operation, Object value) {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Specification<T> specification = (root, query, criteriaBuilder) -> {
			Predicate predicate = null;
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
		return getJpaSpecificationExecutor().findAll(specification);
	}

	@Transactional
	public T save(T entity) {
		String username = currentUserProvider.getCurrentUserName();

		if (entity.getId() == null) {
			entity.setCreatedBy(username);
			entity.setCreatedAt(LocalDateTime.now());
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
