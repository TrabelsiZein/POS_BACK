package com.digithink.pos.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.digithink.pos.model.PurchaseHeader;
import com.digithink.pos.model.PurchaseLine;

@Repository
public interface PurchaseLineRepository extends _BaseRepository<PurchaseLine, Long> {

	List<PurchaseLine> findByPurchaseHeader(PurchaseHeader purchaseHeader);

	void deleteByPurchaseHeader(PurchaseHeader purchaseHeader);
}
