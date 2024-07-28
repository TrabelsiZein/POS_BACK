package com.digithink.business_management.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.service._BaseService;

@RestController
public class _BaseController<T extends _BaseEntity, ID, S extends _BaseService<T, ID>> {

	@Autowired
	protected S service;

	@GetMapping
	public ResponseEntity<?> getAll() {
		try {
			return ResponseEntity.ok(service.findAll());
		} catch (Exception e) {
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable ID id) {
		Optional<T> entity = service.findById(id);
		return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody T entity) {
		T createdEntity = service.save(entity);
		return ResponseEntity.ok(createdEntity);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable ID id) {
		service.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}