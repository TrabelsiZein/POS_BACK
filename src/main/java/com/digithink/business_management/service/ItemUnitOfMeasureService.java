package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.ItemUnitOfMeasure;
import com.digithink.business_management.repository.ItemUnitOfMeasureRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class ItemUnitOfMeasureService extends _BaseService<ItemUnitOfMeasure, Long> {

	@Autowired
	private ItemUnitOfMeasureRepository itemUnitOfMeasureRepository;

	@Override
	protected _BaseRepository<ItemUnitOfMeasure, Long> getRepository() {
		return itemUnitOfMeasureRepository;
	}

}
