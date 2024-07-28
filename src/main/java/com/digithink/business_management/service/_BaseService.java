package com.digithink.business_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.security.CurrentUserProvider;

@Service
public abstract class _BaseService<T extends _BaseEntity, ID> {

	@Autowired
	private CurrentUserProvider currentUserProvider;

	protected abstract JpaRepository<T, ID> getRepository();

	public List<T> findAll() {
		return getRepository().findAll();
	}

	public Optional<T> findById(ID id) {
		return getRepository().findById(id);
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
