package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.SeriesLine;
import com.digithink.business_management.repository.SeriesLineRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class SeriesLineService extends _BaseService<SeriesLine, Long> {

	@Autowired
	private SeriesLineRepository seriesLineRepository;

	@Override
	protected _BaseRepository<SeriesLine, Long> getRepository() {
		return seriesLineRepository;
	}

}
