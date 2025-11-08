package com.digithink.pos.repository;

import java.util.List;
import java.util.Optional;

import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.model.enumeration.ItemType;

public interface ItemRepository extends _BaseRepository<Item, Long> {

	Optional<Item> findByItemCode(String itemCode);

	Optional<Item> findByErpExternalId(String erpExternalId);

	List<Item> findByType(ItemType type);

	List<Item> findByStockQuantityLessThan(Integer quantity);

	Optional<Item> findByBarcode(String barcode);

	List<Item> findByItemFamily(ItemFamily itemFamily);

	List<Item> findByItemSubFamily(ItemSubFamily itemSubFamily);
}
