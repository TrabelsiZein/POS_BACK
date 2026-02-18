package com.digithink.pos.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SalesLine;
import com.digithink.pos.model.Warranty;

@Repository
public interface WarrantyRepository extends _BaseRepository<Warranty, Long> {

	List<Warranty> findBySalesHeader(SalesHeader salesHeader);

	List<Warranty> findBySalesLine(SalesLine salesLine);
}
