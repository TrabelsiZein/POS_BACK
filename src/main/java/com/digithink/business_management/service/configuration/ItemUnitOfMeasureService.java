package com.digithink.business_management.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.ItemUnitOfMeasure;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.configuration.ItemUnitOfMeasureRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class ItemUnitOfMeasureService extends _BaseService<ItemUnitOfMeasure, Long> {

	@Autowired
	private ItemUnitOfMeasureRepository itemUnitOfMeasureRepository;

	@Override
	protected _BaseRepository<ItemUnitOfMeasure, Long> getRepository() {
		return itemUnitOfMeasureRepository;
	}

}
