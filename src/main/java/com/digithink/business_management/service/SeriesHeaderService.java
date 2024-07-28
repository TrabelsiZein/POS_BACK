package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.SeriesHeader;
import com.digithink.business_management.repository.SeriesHeaderRepository;

public class SeriesHeaderService extends _BaseService<SeriesHeader, Long> {

	@Autowired
	private SeriesHeaderRepository seriesHeaderRepository;

	@Override
	protected JpaRepository<SeriesHeader, Long> getRepository() {
		return seriesHeaderRepository;
	}

}
