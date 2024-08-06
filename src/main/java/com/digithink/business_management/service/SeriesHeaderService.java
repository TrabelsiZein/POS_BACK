package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.SeriesHeader;
import com.digithink.business_management.repository.SeriesHeaderRepository;

@Service
public class SeriesHeaderService extends _BaseService<SeriesHeader, Long> {

	@Autowired
	private SeriesHeaderRepository seriesHeaderRepository;

	@Override
	protected JpaRepository<SeriesHeader, Long> getRepository() {
		return seriesHeaderRepository;
	}

	@Override
	protected JpaSpecificationExecutor<SeriesHeader> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}
