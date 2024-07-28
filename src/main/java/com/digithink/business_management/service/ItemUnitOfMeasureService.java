package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.ItemUnitOfMeasure;
import com.digithink.business_management.repository.ItemUnitOfMeasureRepository;

public class ItemUnitOfMeasureService extends _BaseService<ItemUnitOfMeasure, Long> {

	@Autowired
	private ItemUnitOfMeasureRepository itemUnitOfMeasureRepository;

	@Override
	protected JpaRepository<ItemUnitOfMeasure, Long> getRepository() {
		return itemUnitOfMeasureRepository;
	}

}
