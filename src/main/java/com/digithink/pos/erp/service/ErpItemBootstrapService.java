package com.digithink.pos.erp.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository.ItemSubFamilyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpItemBootstrapService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpItemBootstrapService.class);
	private static final String SYSTEM_USER = "ERP_SYNC";

	private final ItemFamilyRepository itemFamilyRepository;
	private final ItemSubFamilyRepository itemSubFamilyRepository;

	@Transactional
	public List<ItemFamily> importItemFamilies(List<ErpItemFamilyDTO> families) {
		if (families == null || families.isEmpty()) {
			return Collections.emptyList();
		}

		List<ItemFamily> persisted = new ArrayList<>();
		for (ErpItemFamilyDTO dto : families) {
			if (dto == null) {
				continue;
			}
			ItemFamily entity = resolveItemFamily(dto);
			boolean isNew = entity.getId() == null;
			applyFamilyValues(entity, dto);
			if (!StringUtils.hasText(entity.getCode())) {
				LOGGER.warn("Skipping item family import because no code/external identifier provided: {}", dto);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(itemFamilyRepository.save(entity));
		}
		return persisted;
	}

	@Transactional
	public List<ItemSubFamily> importItemSubFamilies(List<ErpItemSubFamilyDTO> subFamilies) {
		if (subFamilies == null || subFamilies.isEmpty()) {
			return Collections.emptyList();
		}

		List<ItemSubFamily> persisted = new ArrayList<>();
		for (ErpItemSubFamilyDTO dto : subFamilies) {
			if (dto == null) {
				continue;
			}

			Optional<ItemFamily> familyOpt = resolveFamilyForSubFamily(dto);
			if (!familyOpt.isPresent()) {
				LOGGER.warn("Skipping subfamily with external id {} because parent family {} not found",
						dto.getExternalId(), dto.getFamilyExternalId());
				continue;
			}

			ItemSubFamily entity = resolveItemSubFamily(dto);
			boolean isNew = entity.getId() == null;
			applySubFamilyValues(entity, dto, familyOpt.get());
			if (!StringUtils.hasText(entity.getCode())) {
				LOGGER.warn("Skipping item subfamily import because no code/external identifier provided: {}", dto);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(itemSubFamilyRepository.save(entity));
		}
		return persisted;
	}

	private ItemFamily resolveItemFamily(ErpItemFamilyDTO dto) {
		return findFamilyByExternalOrCode(dto.getExternalId(), dto.getCode())
				.orElseGet(ItemFamily::new);
	}

	private Optional<ItemFamily> findFamilyByExternalOrCode(String externalId, String code) {
		if (externalId != null && !externalId.isEmpty()) {
			Optional<ItemFamily> byExternalId = itemFamilyRepository.findByErpExternalId(externalId);
			if (byExternalId.isPresent()) {
				return byExternalId;
			}
		}
		if (code != null && !code.isEmpty()) {
			return itemFamilyRepository.findByCode(code);
		}
		return Optional.empty();
	}

	private ItemSubFamily resolveItemSubFamily(ErpItemSubFamilyDTO dto) {
		return findSubFamilyByExternalOrCode(dto.getExternalId(), dto.getCode())
				.orElseGet(ItemSubFamily::new);
	}

	private Optional<ItemSubFamily> findSubFamilyByExternalOrCode(String externalId, String code) {
		if (externalId != null && !externalId.isEmpty()) {
			Optional<ItemSubFamily> byExternalId = itemSubFamilyRepository.findByErpExternalId(externalId);
			if (byExternalId.isPresent()) {
				return byExternalId;
			}
		}
		if (code != null && !code.isEmpty()) {
			return itemSubFamilyRepository.findByCode(code);
		}
		return Optional.empty();
	}

	private void applyFamilyValues(ItemFamily entity, ErpItemFamilyDTO dto) {
		entity.setErpExternalId(defaultString(dto.getExternalId()));
		entity.setCode(defaultString(dto.getCode(), dto.getExternalId()));
		entity.setName(defaultString(dto.getName(), dto.getCode(), dto.getExternalId()));
		entity.setDescription(defaultString(dto.getDescription()));
		if (dto.getDisplayOrder() != null) {
			entity.setDisplayOrder(dto.getDisplayOrder());
		} else if (entity.getDisplayOrder() == null) {
			entity.setDisplayOrder(0);
		}
		if (dto.getActive() != null) {
			entity.setActive(dto.getActive());
		}
	}

	private void applySubFamilyValues(ItemSubFamily entity, ErpItemSubFamilyDTO dto, ItemFamily family) {
		entity.setErpExternalId(defaultString(dto.getExternalId()));
		entity.setCode(defaultString(dto.getCode(), dto.getExternalId()));
		entity.setName(defaultString(dto.getName(), dto.getCode(), dto.getExternalId()));
		entity.setDescription(defaultString(dto.getDescription()));
		if (dto.getDisplayOrder() != null) {
			entity.setDisplayOrder(dto.getDisplayOrder());
		} else if (entity.getDisplayOrder() == null) {
			entity.setDisplayOrder(0);
		}
		if (dto.getActive() != null) {
			entity.setActive(dto.getActive());
		}
		entity.setItemFamily(family);
	}

	private Optional<ItemFamily> resolveFamilyForSubFamily(ErpItemSubFamilyDTO dto) {
		String familyExternalId = dto.getFamilyExternalId();
		if (StringUtils.hasText(familyExternalId)) {
			Optional<ItemFamily> byExternal = itemFamilyRepository.findByErpExternalId(familyExternalId);
			if (byExternal.isPresent()) {
				return byExternal;
			}
			Optional<ItemFamily> byCode = itemFamilyRepository.findByCode(familyExternalId);
			if (byCode.isPresent()) {
				return byCode;
			}
		}
		return Optional.empty();
	}

	private String defaultString(String value) {
		return value != null ? value : "";
	}

	private String defaultString(String value, String... fallbacks) {
		if (StringUtils.hasText(value)) {
			return value;
		}
		for (String fallback : fallbacks) {
			if (StringUtils.hasText(fallback)) {
				return fallback;
			}
		}
		return "";
	}
}

