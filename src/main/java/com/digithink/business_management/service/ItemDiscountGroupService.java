package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.ItemDiscountGroup;
import com.digithink.business_management.repository.ItemDiscountGroupRepository;

@Service
public class ItemDiscountGroupService extends _BaseService<ItemDiscountGroup, Long> {

	@Autowired
	private ItemDiscountGroupRepository itemDiscountGroupRepository;

	@Override
	protected JpaRepository<ItemDiscountGroup, Long> getRepository() {
		return itemDiscountGroupRepository;
	}

}
