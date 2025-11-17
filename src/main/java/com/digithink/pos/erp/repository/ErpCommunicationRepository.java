package com.digithink.pos.erp.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.digithink.pos.erp.model.ErpCommunication;
import com.digithink.pos.repository._BaseRepository;

@Repository
public interface ErpCommunicationRepository extends _BaseRepository<ErpCommunication, Long> {

    List<ErpCommunication> findByStartedAtBetweenOrderByStartedAtDesc(LocalDateTime start, LocalDateTime end);
}

