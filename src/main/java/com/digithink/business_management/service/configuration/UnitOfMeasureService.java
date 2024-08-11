package com.digithink.business_management.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.UnitOfMeasure;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.configuration.UnitOfMeasureRepository;
import com.digithink.business_management.service._BaseService;

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
