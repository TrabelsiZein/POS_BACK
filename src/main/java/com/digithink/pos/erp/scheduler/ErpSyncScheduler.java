package com.digithink.pos.erp.scheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import com.digithink.pos.erp.model.ErpSyncJob;
import com.digithink.pos.erp.service.ErpSyncJobRunner;
import com.digithink.pos.erp.service.ErpSyncJobService;
import com.digithink.pos.erp.service.ErpSyncWarningException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ErpSyncScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpSyncScheduler.class);

	private final ErpSyncJobService jobService;
	private final ErpSyncJobRunner jobRunner;

	@Scheduled(fixedDelayString = "${erp.sync.scheduler.delay:60000}")
	public void pollJobs() {
		List<ErpSyncJob> jobs = jobService.findEnabledJobs();
		if (jobs.isEmpty()) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();
		for (ErpSyncJob job : jobs) {
			LocalDateTime nextRun = job.getNextRunAt();
			if (nextRun == null) {
				nextRun = computeNextRun(job, now);
				jobService.updateNextRun(job, nextRun);
				continue;
			}

			if (!nextRun.isAfter(now)) {
				runJob(job, now);
			}
		}
	}

	private void runJob(ErpSyncJob job, LocalDateTime reference) {
		String statusMessage = "SUCCESS";
		try {
			jobRunner.run(job);
		} catch (ErpSyncWarningException warning) {
			statusMessage = "WARNING: " + warning.getMessage();
			LOGGER.warn("ERP sync job {} skipped: {}", job.getJobType(), warning.getMessage());
		} catch (Exception ex) {
			statusMessage = "ERROR: " + ex.getMessage();
			LOGGER.error("ERP sync job {} failed: {}", job.getJobType(), ex.getMessage(), ex);
		}

		LocalDateTime next = computeNextRun(job, reference);
		jobService.markExecution(job, statusMessage, next);
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
}
