package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.ItemSubFamilyRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class ItemService extends _BaseService<Item, Long> {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemFamilyRepository itemFamilyRepository;

	@Autowired
	private ItemSubFamilyRepository itemSubFamilyRepository;

	@Override
	protected _BaseRepository<Item, Long> getRepository() {
		return itemRepository;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Item save(Item item) throws Exception {
		// Ensure family/sub-family references are consistent
		if (item.getItemSubFamily() != null) {
			if (item.getItemSubFamily().getId() == null) {
				throw new IllegalArgumentException("Item sub-family ID is required");
			}
			ItemSubFamily persistedSubFamily = itemSubFamilyRepository.findById(item.getItemSubFamily().getId())
					.orElseThrow(() -> new IllegalArgumentException(
							"Item sub-family not found: " + item.getItemSubFamily().getId()));
			item.setItemSubFamily(persistedSubFamily);
			item.setItemFamily(persistedSubFamily.getItemFamily());
		} else if (item.getItemFamily() != null) {
			if (item.getItemFamily().getId() == null) {
				throw new IllegalArgumentException("Item family ID is required");
			}
			ItemFamily persistedFamily = itemFamilyRepository.findById(item.getItemFamily().getId())
					.orElseThrow(() -> new IllegalArgumentException(
							"Item family not found: " + item.getItemFamily().getId()));
			item.setItemFamily(persistedFamily);
		}

		return super.save(item);
	}
}

