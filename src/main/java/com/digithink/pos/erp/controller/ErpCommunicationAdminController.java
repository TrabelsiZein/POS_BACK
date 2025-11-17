package com.digithink.pos.erp.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digithink.pos.erp.dto.ErpCommunicationViewDTO;
import com.digithink.pos.erp.model.ErpCommunication;
import com.digithink.pos.erp.repository.ErpCommunicationRepository;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.Role;
import com.digithink.pos.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("admin/erp/communications")
@RequiredArgsConstructor
public class ErpCommunicationAdminController {

	private final ErpCommunicationRepository communicationRepository;
	private final CurrentUserProvider currentUserProvider;

	@GetMapping
	public ResponseEntity<List<ErpCommunicationViewDTO>> listCommunications(
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "to", required = false) String to,
			@RequestParam(name = "limit", defaultValue = "200") int limit) {

		ensureAdminAccess();

		if (limit <= 0) {
			limit = 200;
		} else if (limit > 1000) {
			limit = 1000;
		}

		LocalDate fromDate = parseOrDefault(from, LocalDate.now());
		LocalDate toDate = parseOrDefault(to, LocalDate.now());
		if (toDate.isBefore(fromDate)) {
			LocalDate tmp = fromDate;
			fromDate = toDate;
			toDate = tmp;
		}

		LocalDateTime start = fromDate.atStartOfDay();
		LocalDateTime end = toDate.plusDays(1).atStartOfDay();

		List<ErpCommunication> communications = communicationRepository
				.findByStartedAtBetweenOrderByStartedAtDesc(start, end);

		List<ErpCommunicationViewDTO> dtos = communications.stream()
				.limit(limit)
				.map(this::mapToDto)
				.collect(Collectors.toList());

		return ResponseEntity.ok(dtos);
	}

	private ErpCommunicationViewDTO mapToDto(ErpCommunication entity) {
		ErpCommunicationViewDTO dto = new ErpCommunicationViewDTO();
		dto.setId(entity.getId());
		dto.setOperation(entity.getOperation());
		dto.setStatus(entity.getStatus());
		dto.setExternalReference(entity.getExternalReference());
		dto.setErrorMessage(entity.getErrorMessage());
		dto.setStartedAt(entity.getStartedAt());
		dto.setCompletedAt(entity.getCompletedAt());
		dto.setDurationMs(entity.getDurationMs());
		dto.setRequestPayload(entity.getRequestPayload());
		dto.setResponsePayload(entity.getResponsePayload());
		return dto;
	}

	private LocalDate parseOrDefault(String value, LocalDate defaultDate) {
		if (value == null || value.isBlank()) {
			return defaultDate;
		}
		try {
			return LocalDate.parse(value);
		} catch (Exception ex) {
			return defaultDate;
		}
	}

	private void ensureAdminAccess() {
		UserAccount currentUser = currentUserProvider.getCurrentUser();
		if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin privileges required");
		}
	}
}


