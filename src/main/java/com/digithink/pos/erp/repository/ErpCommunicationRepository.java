package com.digithink.pos.erp.repository;

import org.springframework.stereotype.Repository;

import com.digithink.pos.erp.model.ErpCommunication;
import com.digithink.pos.repository._BaseRepository;

@Repository
public interface ErpCommunicationRepository extends _BaseRepository<ErpCommunication, Long> {
}

