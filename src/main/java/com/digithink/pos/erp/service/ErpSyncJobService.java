package com.digithink.pos.erp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.digithink.pos.erp.model.ErpSyncJob;
import com.digithink.pos.erp.repository.ErpSyncJobRepository;
import com.digithink.pos.repository._BaseRepository;
import com.digithink.pos.service._BaseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpSyncJobService extends _BaseService<ErpSyncJob, Long> {

	private final ErpSyncJobRepository jobRepository;

	@Override
	protected _BaseRepository<ErpSyncJob, Long> getRepository() {
		return jobRepository;
	}

	public List<ErpSyncJob> findEnabledJobs() {
		return jobRepository.findByEnabledTrueOrderByJobTypeAsc();
	}

	public void markExecution(ErpSyncJob job, String status, LocalDateTime nextRun) {
		job.setLastRunAt(LocalDateTime.now());
		job.setLastStatus(status);
		job.setNextRunAt(nextRun);
		job.setUpdatedAt(LocalDateTime.now());
		job.setUpdatedBy("System");
		jobRepository.save(job);
	}

	public void updateNextRun(ErpSyncJob job, LocalDateTime nextRun) {
		job.setNextRunAt(nextRun);
		job.setUpdatedAt(LocalDateTime.now());
		job.setUpdatedBy("System");
		jobRepository.save(job);
	}
}
