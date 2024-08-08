package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.SeriesHeader;
import com.digithink.business_management.repository.SeriesHeaderRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class SeriesHeaderService extends _BaseService<SeriesHeader, Long> {

	@Autowired
	private SeriesHeaderRepository seriesHeaderRepository;

	@Override
	protected _BaseRepository<SeriesHeader, Long> getRepository() {
		return seriesHeaderRepository;
	}

}
