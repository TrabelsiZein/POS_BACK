package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.SalesLine;
import com.digithink.pos.repository.SalesLineRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class SalesLineService extends _BaseService<SalesLine, Long> {

	@Autowired
	private SalesLineRepository salesLineRepository;

	@Override
	protected _BaseRepository<SalesLine, Long> getRepository() {
		return salesLineRepository;
	}
}

