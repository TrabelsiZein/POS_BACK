package com.digithink.pos.erp.dynamicsnav.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavBarcodeDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCustomerDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavStockKeepingUnitDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSubFamilyDTO;

@Component
public class DynamicsNavMapper {

	public List<ErpItemFamilyDTO> toItemFamilyDTOs(List<DynamicsNavFamilyDTO> navFamilies) {
		if (navFamilies == null) {
			return List.of();
		}
		return navFamilies.stream().map(this::toItemFamilyDTO).toList();
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
		return navSubFamilies.stream().map(this::toItemSubFamilyDTO).toList();
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
		return navLocations.stream().map(this::toLocationDTO).toList();
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
		return navItems.stream().map(this::toItemDTO).toList();
	}

	public ErpItemDTO toItemDTO(DynamicsNavStockKeepingUnitDTO navItem) {
		ErpItemDTO dto = new ErpItemDTO();
		dto.setExternalId(navItem.getItemNo());
		dto.setCode(navItem.getItemNo());
		dto.setName(navItem.getDescription());
		dto.setDescription(navItem.getDescription());
		dto.setSubFamilyExternalId(navItem.getSubFamily());
		dto.setUnitPrice(navItem.getUnitPrice());
		dto.setDefaultVAT(navItem.getDefaultVAT());
		dto.setActive(true);
		dto.setLastModifiedAt(navItem.getModifiedAt());
		return dto;
	}

	public List<ErpItemBarcodeDTO> toItemBarcodeDTOs(List<DynamicsNavBarcodeDTO> navBarcodes) {
		if (navBarcodes == null) {
			return List.of();
		}
		return navBarcodes.stream().map(this::toItemBarcodeDTO).toList();
	}

	public ErpItemBarcodeDTO toItemBarcodeDTO(DynamicsNavBarcodeDTO navBarcode) {
		ErpItemBarcodeDTO dto = new ErpItemBarcodeDTO();
		dto.setExternalId(navBarcode.getCrossReferenceNo());
		dto.setItemExternalId(navBarcode.getItemNo());
		dto.setBarcode(navBarcode.getCrossReferenceNo());
		dto.setLastModifiedAt(navBarcode.getModifiedAt());
		return dto;
	}

	public List<ErpCustomerDTO> toCustomerDTOs(List<DynamicsNavCustomerDTO> navCustomers) {
		if (navCustomers == null) {
			return List.of();
		}
		return navCustomers.stream().map(this::toCustomerDTO).toList();
	}

	public ErpCustomerDTO toCustomerDTO(DynamicsNavCustomerDTO navCustomer) {
		ErpCustomerDTO dto = new ErpCustomerDTO();
		dto.setExternalId(navCustomer.getNumber());
		dto.setCode(navCustomer.getNumber());
		dto.setName(navCustomer.getName());
		dto.setEmail(navCustomer.getEmail());
		dto.setPhone(navCustomer.getPhoneNumber());
		dto.setAddress(buildFullAddress(navCustomer));
		dto.setCity(navCustomer.getCity());
		dto.setCountry(navCustomer.getCountryRegionCode());
		dto.setTaxNumber(navCustomer.getVatRegistrationNumber());
		dto.setActive(isCustomerActive(navCustomer));
		return dto;
	}

	private boolean isCustomerActive(DynamicsNavCustomerDTO navCustomer) {
		String blocked = navCustomer.getBlocked();
		return blocked == null || blocked.isBlank() || "0".equals(blocked) || "No".equalsIgnoreCase(blocked);
	}

	private String buildFullAddress(DynamicsNavCustomerDTO navCustomer) {
		StringBuilder builder = new StringBuilder();
		if (navCustomer.getAddress() != null && !navCustomer.getAddress().isBlank()) {
			builder.append(navCustomer.getAddress());
		}
		if (navCustomer.getAddress2() != null && !navCustomer.getAddress2().isBlank()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(navCustomer.getAddress2());
		}
		if (navCustomer.getPostCode() != null && !navCustomer.getPostCode().isBlank()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(navCustomer.getPostCode());
		}
		if (navCustomer.getCity() != null && !navCustomer.getCity().isBlank()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(navCustomer.getCity());
		}
		return builder.length() == 0 ? null : builder.toString();
	}
}
