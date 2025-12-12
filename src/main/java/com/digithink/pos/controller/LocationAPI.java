package com.digithink.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.pos.model.Location;
import com.digithink.pos.service.LocationService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("location")
@Log4j2
public class LocationAPI extends _BaseController<Location, Long, LocationService> {

	@PutMapping("/{id}/set-default")
	public ResponseEntity<?> setAsDefault(@PathVariable Long id) {
		try {
			log.info(this.getClass().getSimpleName() + "::setAsDefault::" + id);
			Location location = service.setAsDefault(id);
			return ResponseEntity.ok(location);
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::setAsDefault:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorResponse(detailedMessage));
		}
	}
}

