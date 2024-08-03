package com.digithink.business_management.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.business_management.model._BaseEntity;
import com.digithink.business_management.service._BaseService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public abstract class _BaseController<T extends _BaseEntity, ID, S extends _BaseService<T, ID>> {

	@Autowired
	protected S service;

	@GetMapping
	public ResponseEntity<?> getAll() {
		try {
			log.info(this.getClass().getSimpleName() + "::getAll");
			return ResponseEntity.ok(service.findAll());
		} catch (Exception e) {
			log.info(this.getClass().getSimpleName() + "::getAll::error ::" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable ID id) {
		try {
			log.info(this.getClass().getSimpleName() + "::getById::" + id);
			Optional<T> entity = service.findById(id);
			return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception e) {
			log.info(this.getClass().getSimpleName() + "::getById::error ::" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody T entity) {
		try {
			log.info(this.getClass().getSimpleName() + "::create");
			T createdEntity = service.save(entity);
			return ResponseEntity.ok(createdEntity);
		} catch (Exception e) {
			log.info(this.getClass().getSimpleName() + "::create::error ::" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable ID id) {

		try {
			log.info(this.getClass().getSimpleName() + "::deleteById ::" + id);
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			log.info(this.getClass().getSimpleName() + "::create::error ::" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}