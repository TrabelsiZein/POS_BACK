package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.Item;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.SalesLine;

public interface SalesLineRepository extends _BaseRepository<SalesLine, Long> {

	List<SalesLine> findBySalesHeader(SalesHeader salesHeader);

	List<SalesLine> findByItem(Item item);
	
	void deleteBySalesHeader(SalesHeader salesHeader);
}

