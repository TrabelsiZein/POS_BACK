package com.digithink.pos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemBarcode;
import com.digithink.pos.repository.ItemBarcodeRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class ItemBarcodeService extends _BaseService<ItemBarcode, Long> {

	@Autowired
	private ItemBarcodeRepository itemBarcodeRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Override
	protected _BaseRepository<ItemBarcode, Long> getRepository() {
		return itemBarcodeRepository;
	}

	/**
	 * Get all barcodes for an item
	 */
	public List<ItemBarcode> getBarcodesByItemId(Long itemId) {
		return itemBarcodeRepository.findByItemIdAndActiveTrue(itemId);
	}

	/**
	 * Get item by barcode
	 */
	public Optional<Item> getItemByBarcode(String barcode) {
		Optional<ItemBarcode> itemBarcode = itemBarcodeRepository.findByBarcode(barcode);
		if (itemBarcode.isPresent() && itemBarcode.get().getActive() != null && itemBarcode.get().getActive()) {
			return Optional.of(itemBarcode.get().getItem());
		}
		return Optional.empty();
	}

	/**
	 * Get primary barcode for an item
	 */
	public Optional<ItemBarcode> getPrimaryBarcode(Long itemId) {
		return itemBarcodeRepository.findByItemIdAndIsPrimaryTrue(itemId);
	}
}

