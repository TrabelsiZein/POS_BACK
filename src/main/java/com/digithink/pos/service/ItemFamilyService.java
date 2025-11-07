package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class ItemFamilyService extends _BaseService<ItemFamily, Long> {

	@Autowired
	private ItemFamilyRepository itemFamilyRepository;

	@Override
	protected _BaseRepository<ItemFamily, Long> getRepository() {
		return itemFamilyRepository;
	}
}


