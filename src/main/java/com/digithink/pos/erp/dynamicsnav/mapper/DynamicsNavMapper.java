package com.digithink.pos.erp.dynamicsnav.mapper;

import java.util.List;
import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavBarcodeDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavStockKeepingUnitDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;

@Component
public class DynamicsNavMapper {

	public List<ErpItemFamilyDTO> toItemFamilyDTOs(List<DynamicsNavFamilyDTO> navFamilies) {
		if (navFamilies == null) {
			return List.of();
		}
		return navFamilies.stream()
				.map(this::toItemFamilyDTO)
				.toList();
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

	public List<ErpItemSubFamilyDTO> toItemSubFamilyDTOs(List<DynamicsNavSubFamilyDTO> navSubFamilies) {
		if (navSubFamilies == null) {
			return List.of();
		}
		return navSubFamilies.stream()
				.map(this::toItemSubFamilyDTO)
				.toList();
	}

	public ErpItemSubFamilyDTO toItemSubFamilyDTO(DynamicsNavSubFamilyDTO navSubFamily) {
		ErpItemSubFamilyDTO dto = new ErpItemSubFamilyDTO();
		dto.setExternalId(navSubFamily.getCode());
		dto.setCode(navSubFamily.getCode());
		dto.setName(navSubFamily.getDescription());
		dto.setDescription(navSubFamily.getDescription());
		dto.setActive(true);
		dto.setFamilyExternalId(navSubFamily.getFamilyCode());
		return dto;
	}

	public List<ErpLocationDTO> toLocationDTOs(List<DynamicsNavLocationDTO> navLocations) {
		if (navLocations == null) {
			return List.of();
		}
		return navLocations.stream()
				.map(this::toLocationDTO)
				.toList();
	}

	public ErpLocationDTO toLocationDTO(DynamicsNavLocationDTO navLocation) {
		ErpLocationDTO dto = new ErpLocationDTO();
		dto.setExternalId(navLocation.getCode());
		dto.setCode(navLocation.getCode());
		dto.setName(navLocation.getName());
		dto.setAddress(navLocation.getAddress());
		dto.setCity(navLocation.getCity());
		dto.setCountry(navLocation.getCountryRegionCode());
		dto.setActive(navLocation.getBlocked() == null ? Boolean.TRUE : !navLocation.getBlocked());
		return dto;
	}

	public List<ErpItemDTO> toItemDTOs(List<DynamicsNavStockKeepingUnitDTO> navItems) {
		if (navItems == null) {
			return List.of();
		}
		return navItems.stream()
				.map(this::toItemDTO)
				.toList();
	}

	public ErpItemDTO toItemDTO(DynamicsNavStockKeepingUnitDTO navItem) {
		ErpItemDTO dto = new ErpItemDTO();
		dto.setExternalId(navItem.getItemNo());
		dto.setCode(navItem.getItemNo());
		dto.setName(navItem.getDescription());
		dto.setDescription(navItem.getDescription());
		dto.setFamilyExternalId(navItem.getItemCategoryCode());
		dto.setSubFamilyExternalId(navItem.getProductGroupCode());
		dto.setSalesPrice(navItem.getUnitPrice());
		dto.setCostPrice(navItem.getUnitCost());
		dto.setActive(navItem.getBlocked() == null ? Boolean.TRUE : !navItem.getBlocked());
		dto.setLastModifiedAt(navItem.getModifiedAt());
		return dto;
	}

	public List<ErpItemBarcodeDTO> toItemBarcodeDTOs(List<DynamicsNavBarcodeDTO> navBarcodes) {
		if (navBarcodes == null) {
			return List.of();
		}
		return navBarcodes.stream()
				.map(this::toItemBarcodeDTO)
				.toList();
	}

	public ErpItemBarcodeDTO toItemBarcodeDTO(DynamicsNavBarcodeDTO navBarcode) {
		ErpItemBarcodeDTO dto = new ErpItemBarcodeDTO();
		dto.setExternalId(navBarcode.getBarcode());
		dto.setItemExternalId(navBarcode.getItemNo());
		dto.setBarcode(navBarcode.getBarcode());
		dto.setUnitOfMeasure(navBarcode.getUnitOfMeasureCode());
		Boolean primaryFlag = navBarcode.getDefaultBarcode();
		if (primaryFlag == null) {
			primaryFlag = navBarcode.getPrimary();
		}
		dto.setPrimaryBarcode(primaryFlag != null ? primaryFlag : Boolean.FALSE);
		dto.setLastModifiedAt(navBarcode.getModifiedAt());
		return dto;
	}
}

