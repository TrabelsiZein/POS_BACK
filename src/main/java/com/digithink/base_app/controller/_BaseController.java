package com.digithink.base_app.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.base_app.model._BaseEntity;
import com.digithink.base_app.service.__BaseService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public abstract class _BaseController<T extends _BaseEntity, ID, S extends __BaseService<T, ID>> {

	@Autowired
	protected S service;

	@GetMapping
	public ResponseEntity<?> getAll() {
		try {
			log.info(this.getClass().getSimpleName() + "::getAll");
			return ResponseEntity.ok(service.findAll());
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::getAll:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable ID id) {
		try {
			log.info(this.getClass().getSimpleName() + "::getById::" + id);
			Optional<T> entity = service.findById(id);
			return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::getById:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}

	@GetMapping("/findByField")
	public ResponseEntity<?> findByField(@RequestParam String fieldName, @RequestParam String operation,
			@RequestParam Object value) {
		try {
			log.info(this.getClass().getSimpleName() + "::findByField::" + fieldName + operation + value.toString());
			return ResponseEntity.ok(service.findByField(fieldName, operation, value));
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::findByField:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody T entity) {
		try {
			log.info(this.getClass().getSimpleName() + "::create");
			T createdEntity = service.save(entity);
			return ResponseEntity.ok(createdEntity);
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::create:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteById(@PathVariable ID id) {

		try {
			log.info(this.getClass().getSimpleName() + "::deleteById ::" + id);
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::deleteById:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}

	protected String getDetailedMessage(Throwable e) {
		Throwable cause = e.getCause();
		while (cause != null && cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause != null ? cause.getLocalizedMessage() : e.getLocalizedMessage();
	}
}