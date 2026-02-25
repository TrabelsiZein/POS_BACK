package com.digithink.pos.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.digithink.pos.model.PurchaseHeader;

@Repository
public interface PurchaseHeaderRepository extends _BaseRepository<PurchaseHeader, Long> {

	Optional<PurchaseHeader> findByPurchaseNumber(String purchaseNumber);
}
