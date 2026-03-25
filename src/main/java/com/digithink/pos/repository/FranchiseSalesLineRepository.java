package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.FranchiseSalesLine;

public interface FranchiseSalesLineRepository extends _BaseRepository<FranchiseSalesLine, Long> {

	List<FranchiseSalesLine> findByHeaderId(Long headerId);
}
