package com.digithink.pos.erp.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.digithink.pos.erp.dto.ErpSyncJobViewDTO;
import com.digithink.pos.erp.enumeration.ErpSyncJobType;
import com.digithink.pos.erp.model.ErpSyncJob;
import com.digithink.pos.erp.service.ErpSyncCheckpointService;
import com.digithink.pos.erp.service.ErpSyncJobRunner;
import com.digithink.pos.erp.service.ErpSyncJobService;
import com.digithink.pos.erp.service.ErpSyncWarningException;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.Role;
import com.digithink.pos.security.CurrentUserProvider;
import com.digithink.pos.service.GeneralSetupService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin/erp/jobs")
@RequiredArgsConstructor
public class ErpSyncJobAdminController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpSyncJobAdminController.class);

	private final ErpSyncJobService jobService;
	private final ErpSyncCheckpointService checkpointService;
	private final GeneralSetupService generalSetupService;
	private final CurrentUserProvider currentUserProvider;
	private final ErpSyncJobRunner jobRunner;

	@GetMapping
	public ResponseEntity<List<ErpSyncJobViewDTO>> getJobs() {
		ensureAdminAccess();

		List<ErpSyncJob> jobs = new ArrayList<>(jobService.findAll());
		jobs.sort(Comparator.comparing(ErpSyncJob::getJobType));
		Map<ErpSyncJobType, String> checkpointCodes = ErpSyncCheckpointService.getCheckpointCodes();

		List<ErpSyncJobViewDTO> response = new ArrayList<>(jobs.size());
		for (ErpSyncJob job : jobs) {
			response.add(mapToDto(job, checkpointCodes));
		}

		return ResponseEntity.ok(response);
	}

	@PostMapping("{id}/run")
	public ResponseEntity<?> runJobNow(@PathVariable Long id) {
		ensureAdminAccess();

		ErpSyncJob job = jobService.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

		LocalDateTime reference = LocalDateTime.now();
		try {
			jobRunner.run(job);
			LocalDateTime nextRun = computeNextRun(job, reference);
			jobService.markExecution(job, "SUCCESS", nextRun);
			return ResponseEntity.ok(mapToDto(job));
		} catch (ErpSyncWarningException warning) {
			LOGGER.warn("ERP job {} run triggered warning: {}", job.getJobType(), warning.getMessage());
			LocalDateTime nextRun = computeNextRun(job, reference);
			jobService.markExecution(job, "WARNING", nextRun);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(java.util.Collections.singletonMap("error", warning.getMessage()));
		} catch (Exception ex) {
			LOGGER.error("Failed to execute ERP job {} immediately: {}", job.getJobType(), ex.getMessage(), ex);
			LocalDateTime nextRun = computeNextRun(job, reference);
			jobService.markExecution(job, "ERROR", nextRun);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex);
		}
	}

	private ErpSyncJobViewDTO mapToDto(ErpSyncJob job) {
		return mapToDto(job, ErpSyncCheckpointService.getCheckpointCodes());
	}

	private ErpSyncJobViewDTO mapToDto(ErpSyncJob job, Map<ErpSyncJobType, String> checkpointCodes) {
		ErpSyncJobViewDTO dto = new ErpSyncJobViewDTO();
		dto.setId(job.getId());
		dto.setJobType(job.getJobType());
		dto.setDescription(job.getDescription());
		dto.setCronExpression(job.getCronExpression());
		dto.setEnabled(job.getEnabled());
		dto.setLastRunAt(job.getLastRunAt());
		dto.setNextRunAt(job.getNextRunAt());
		dto.setLastStatus(job.getLastStatus());

		String checkpointCode = checkpointCodes.get(job.getJobType());
		dto.setCheckpointCode(checkpointCode);
		if (checkpointCode != null) {
			dto.setCheckpointValue(generalSetupService.findValueByCode(checkpointCode));
			checkpointService.resolveLastSync(job.getJobType()).ifPresent(dto::setLastCheckpointAt);
		}

		return dto;
	}

	private LocalDateTime computeNextRun(ErpSyncJob job, LocalDateTime reference) {
		try {
			CronSequenceGenerator generator = new CronSequenceGenerator(job.getCronExpression());
			Instant nextInstant = generator.next(toDate(reference)).toInstant();
			return LocalDateTime.ofInstant(nextInstant, ZoneId.systemDefault());
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Invalid cron expression '{}' for job {}. {}", job.getCronExpression(), job.getJobType(),
					ex.getMessage());
			return reference.plusMinutes(10);
		}
	}

	private java.util.Date toDate(LocalDateTime dateTime) {
		return java.util.Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private void ensureAdminAccess() {
		UserAccount currentUser = currentUserProvider.getCurrentUser();
		if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin privileges required");
		}
	}
}
