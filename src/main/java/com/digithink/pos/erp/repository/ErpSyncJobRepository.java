package com.digithink.pos.erp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.digithink.pos.erp.enumeration.ErpSyncJobType;
import com.digithink.pos.erp.model.ErpSyncJob;
import com.digithink.pos.repository._BaseRepository;

@Repository
public interface ErpSyncJobRepository extends _BaseRepository<ErpSyncJob, Long> {

	Optional<ErpSyncJob> findByJobType(ErpSyncJobType jobType);

	List<ErpSyncJob> findByEnabledTrueOrderByJobNameAsc();

	List<ErpSyncJob> findByJobTypeAndEnabledTrue(ErpSyncJobType jobType);
}
