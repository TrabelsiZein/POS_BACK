package com.digithink.pos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.repository.ItemSubFamilyRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class ItemSubFamilyService extends _BaseService<ItemSubFamily, Long> {

	@Autowired
	private ItemSubFamilyRepository itemSubFamilyRepository;

	@Override
	protected _BaseRepository<ItemSubFamily, Long> getRepository() {
		return itemSubFamilyRepository;
	}

	public List<ItemSubFamily> findByFamily(ItemFamily family) {
		return itemSubFamilyRepository.findByItemFamily(family);
	}
}


