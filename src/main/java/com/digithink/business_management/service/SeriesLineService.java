package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.SeriesLine;
import com.digithink.business_management.repository.SeriesLineRepository;

@Service
public class SeriesLineService extends _BaseService<SeriesLine, Long> {

	@Autowired
	private SeriesLineRepository seriesLineRepository;

	@Override
	protected JpaRepository<SeriesLine, Long> getRepository() {
		return seriesLineRepository;
	}

	@Override
	protected JpaSpecificationExecutor<SeriesLine> getJpaSpecificationExecutor() {
		return seriesLineRepository;
	}

}
