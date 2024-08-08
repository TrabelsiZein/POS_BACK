package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.UnitOfMeasure;
import com.digithink.business_management.repository.UnitOfMeasureRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class UnitOfMeasureService extends _BaseService<UnitOfMeasure, Long> {

	@Autowired
	private UnitOfMeasureRepository unitOfMeasureRepository;

	@Override
	protected _BaseRepository<UnitOfMeasure, Long> getRepository() {
		// TODO Auto-generated method stub
		return unitOfMeasureRepository;
	}

}
