package com.digithink.pos.erp.dynamicsnav.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;

@Component
public class DynamicsNavMapper {

	public List<ErpItemFamilyDTO> toItemFamilyDTOs(List<DynamicsNavFamilyDTO> navFamilies) {
		if (navFamilies == null) {
			return List.of();
		}
		return navFamilies.stream()
				.map(this::toItemFamilyDTO)
				.collect(Collectors.toList());
	}

	public ErpItemFamilyDTO toItemFamilyDTO(DynamicsNavFamilyDTO navFamily) {
		ErpItemFamilyDTO dto = new ErpItemFamilyDTO();
		dto.setExternalId(navFamily.getCode());
		dto.setCode(navFamily.getCode());
		dto.setName(navFamily.getDescription());
		dto.setDescription(navFamily.getDescription());
		dto.setActive(true);
		return dto;
	}
}

