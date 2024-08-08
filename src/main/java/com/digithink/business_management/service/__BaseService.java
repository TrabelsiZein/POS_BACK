package com.digithink.business_management.service;

import java.util.List;
import java.util.Optional;

import com.digithink.business_management.model._BaseSysEntity;

public interface __BaseService<T extends _BaseSysEntity, ID> {

	public List<T> findAll();

	public Optional<T> findById(ID id);

	public List<T> findByField(String fieldName, String operation, Object value);

	public T save(T entity);

	public void deleteById(ID id);
}
