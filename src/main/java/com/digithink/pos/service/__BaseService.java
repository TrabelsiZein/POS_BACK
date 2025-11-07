package com.digithink.vacation_app.service;

import java.util.List;
import java.util.Optional;

import com.digithink.vacation_app.model._BaseEntity;

public interface __BaseService<T extends _BaseEntity, ID> {

	public List<T> findAll();

	public Optional<T> findById(ID id);

	public List<T> findByField(String fieldName, String operation, Object value);

	public T save(T entity) throws Exception;

	public void deleteById(ID id);
}
