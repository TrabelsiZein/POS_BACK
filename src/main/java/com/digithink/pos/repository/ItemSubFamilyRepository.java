package com.digithink.pos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;

@Repository
public interface ItemSubFamilyRepository extends _BaseRepository<ItemSubFamily, Long> {

	Optional<ItemSubFamily> findByCode(String code);

	List<ItemSubFamily> findByItemFamily(ItemFamily itemFamily);
}


