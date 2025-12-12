package com.digithink.pos.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digithink.pos.erp.enumeration.ErpSyncOperation;
import com.digithink.pos.erp.model.ErpCommunication;
import com.digithink.pos.repository._BaseRepository;

@Repository
public interface ErpCommunicationRepository extends _BaseRepository<ErpCommunication, Long> {

    List<ErpCommunication> findByStartedAtBetweenOrderByStartedAtDesc(LocalDateTime start, LocalDateTime end);

    List<ErpCommunication> findByOperationAndStartedAtBetweenOrderByStartedAtDesc(
            ErpSyncOperation operation, LocalDateTime start, LocalDateTime end);

    List<ErpCommunication> findByOperationInAndStartedAtBetweenOrderByStartedAtDesc(
            List<ErpSyncOperation> operations, LocalDateTime start, LocalDateTime end);

    List<ErpCommunication> findByOperationOrderByStartedAtDesc(ErpSyncOperation operation);

    List<ErpCommunication> findByOperationInOrderByStartedAtDesc(List<ErpSyncOperation> operations);
}

