package com.digithink.vacation_app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digithink.vacation_app.model.VacationRequest;
import com.digithink.vacation_app.service.VacationRequestService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("vacation_request")
@Log4j2
public class VacationRequestAPI extends _BaseController<VacationRequest, Long, VacationRequestService> {

	@Autowired
	private VacationRequestService requestService;

	@GetMapping("/mine")
	public ResponseEntity<?> getAllByCurrentUser() {
		try {
			log.info(this.getClass().getSimpleName() + "::getAllByCurrentUser");
			return ResponseEntity.ok(requestService.findByCurrentUser());
		} catch (Exception e) {
			String detailedMessage = getDetailedMessage(e);
			log.error(this.getClass().getSimpleName() + "::getAllByCurrentUser:error: " + detailedMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detailedMessage);
		}
	}
}
