package com.digithink.pos.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

	public Page<Item> findActiveItems(String search, String filterType, Pageable pageable) {
		Specification<Item> specification = (root, query, cb) -> {
			query.distinct(true);
			Predicate predicate = cb.conjunction();
			predicate = cb.and(predicate, cb.isTrue(root.get("active")));

			if (StringUtils.hasText(search)) {
				String likeValue = "%" + search.toLowerCase() + "%";
				predicate = cb.and(predicate, cb.or(cb.like(cb.lower(root.get("itemCode")), likeValue),
						cb.like(cb.lower(root.get("name")), likeValue)));
			}

			if ("withBarcodes".equalsIgnoreCase(filterType) || "withoutBarcodes".equalsIgnoreCase(filterType)) {
				Subquery<Long> subquery = query.subquery(Long.class);
				var barcodeRoot = subquery.from(com.digithink.pos.model.ItemBarcode.class);
				subquery.select(barcodeRoot.get("item").get("id"));
				subquery.where(cb.equal(barcodeRoot.get("item").get("id"), root.get("id")),
						cb.isTrue(barcodeRoot.get("active")));

				if ("withBarcodes".equalsIgnoreCase(filterType)) {
					predicate = cb.and(predicate, cb.exists(subquery));
				} else {
					predicate = cb.and(predicate, cb.not(cb.exists(subquery)));
				}
			}

			return predicate;
		};

		return itemRepository.findAll(specification, pageable);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Item save(Item item) throws Exception {
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
			ItemFamily persistedFamily = itemFamilyRepository.findById(item.getItemFamily().getId()).orElseThrow(
					() -> new IllegalArgumentException("Item family not found: " + item.getItemFamily().getId()));
			item.setItemFamily(persistedFamily);
		}

		return super.save(item);
	}

	public List<Item> findActiveByFamilyId(Long familyId) {
		ItemFamily family = itemFamilyRepository.findById(familyId)
				.orElseThrow(() -> new IllegalArgumentException("Item family not found: " + familyId));

		return itemRepository.findByItemFamily(family).stream()
				.filter(item -> item.getActive() == null || Boolean.TRUE.equals(item.getActive()))
				.collect(Collectors.toList());
	}

	public List<Item> findActiveBySubFamilyId(Long subFamilyId) {
		ItemSubFamily subFamily = itemSubFamilyRepository.findById(subFamilyId)
				.orElseThrow(() -> new IllegalArgumentException("Item sub-family not found: " + subFamilyId));

		return itemRepository.findByItemSubFamily(subFamily).stream()
				.filter(item -> item.getActive() == null || Boolean.TRUE.equals(item.getActive()))
				.collect(Collectors.toList());
	}
}

