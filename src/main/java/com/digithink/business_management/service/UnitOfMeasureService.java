package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.UnitOfMeasure;
import com.digithink.business_management.repository.UnitOfMeasureRepository;

public class UnitOfMeasureService extends _BaseService<UnitOfMeasure, Long> {

	@Autowired
	private UnitOfMeasureRepository unitOfMeasureRepository;

	@Override
	protected JpaRepository<UnitOfMeasure, Long> getRepository() {
		// TODO Auto-generated method stub
		return unitOfMeasureRepository;
	}

}
