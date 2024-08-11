package com.digithink.business_management.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.ItemDiscountGroup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.configuration.ItemDiscountGroupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class ItemDiscountGroupService extends _BaseService<ItemDiscountGroup, Long> {

	@Autowired
	private ItemDiscountGroupRepository itemDiscountGroupRepository;

	@Override
	protected _BaseRepository<ItemDiscountGroup, Long> getRepository() {
		return itemDiscountGroupRepository;
	}

}
