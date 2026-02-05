package com.digithink.pos.erp.dynamicsnav.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.dto.ErpReturnDTO;
import com.digithink.pos.erp.dto.ErpReturnLineDTO;
import com.digithink.pos.erp.dto.ErpSalesDiscountDTO;
import com.digithink.pos.erp.dto.ErpSalesPriceDTO;
import com.digithink.pos.erp.dto.ErpSessionDTO;
import com.digithink.pos.erp.dto.ErpTicketDTO;
import com.digithink.pos.erp.dto.ErpTicketLineDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavBarcodeDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCustomerDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavReturnHeaderDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavReturnLineDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesDiscountDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderHeaderDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderLineDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesPriceDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSessionDTO;
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
		dto.setResponsibilityCenter(navLocation.getResponsibilityCenter());
//		dto.setAddress(navLocation.getAddress());
//		dto.setCity(navLocation.getCity());
//		dto.setCountry(navLocation.getCountryRegionCode());
//		dto.setActive(navLocation.getBlocked() == null ? Boolean.TRUE : !navLocation.getBlocked());
		dto.setActive(Boolean.TRUE);
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
		dto.setItemDiscGroup(navItem.getItemDiscGroup());
		dto.setMaximumAuthorizedDiscount(navItem.getMaximumAuthorizedDiscount());
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
		dto.setLastModifiedAt(navCustomer.getLastModifiedAt());
		dto.setActive(navCustomer.getStatus().equals("Active"));
		dto.setCustomerPriceGroup(navCustomer.getCustomerPriceGroup());
		dto.setCustomerDiscGroup(navCustomer.getCustomerDiscGroup());
		dto.setAuxiliaryIndex1(navCustomer.getAuxiliaryIndex1());
		return dto;
	}

	private String buildFullAddress(DynamicsNavCustomerDTO navCustomer) {
		StringBuilder builder = new StringBuilder();
		if (navCustomer.getAddress() != null && !navCustomer.getAddress().isBlank()) {
			builder.append(navCustomer.getAddress());
		}
//		if (navCustomer.getAddress2() != null && !navCustomer.getAddress2().isBlank()) {
//			if (builder.length() > 0) {
//				builder.append(", ");
//			}
//			builder.append(navCustomer.getAddress2());
//		}
//		if (navCustomer.getPostCode() != null && !navCustomer.getPostCode().isBlank()) {
//			if (builder.length() > 0) {
//				builder.append(", ");
//			}
//			builder.append(navCustomer.getPostCode());
//		}
//		if (navCustomer.getCity() != null && !navCustomer.getCity().isBlank()) {
//			if (builder.length() > 0) {
//				builder.append(", ");
//			}
//			builder.append(navCustomer.getCity());
//		}
		return builder.length() == 0 ? null : builder.toString();
	}

	public List<ErpSalesPriceDTO> toSalesPriceDTOs(List<DynamicsNavSalesPriceDTO> navSalesPrices) {
		if (navSalesPrices == null) {
			return List.of();
		}
		return navSalesPrices.stream().map(this::toSalesPriceDTO).toList();
	}

	public ErpSalesPriceDTO toSalesPriceDTO(DynamicsNavSalesPriceDTO navSalesPrice) {
		ErpSalesPriceDTO dto = new ErpSalesPriceDTO();
		// Build composite externalId: Item_No + Sales_Type + Sales_Code +
		// Responsibility_Center + Starting_Date + Currency_Code + Variant_Code +
		// Unit_of_Measure_Code + Minimum_Quantity
		String externalId = buildSalesPriceExternalId(navSalesPrice.getItemNo(), navSalesPrice.getSalesType(),
				navSalesPrice.getSalesCode(), navSalesPrice.getResponsibilityCenter(), navSalesPrice.getStartingDate(),
				navSalesPrice.getCurrencyCode(), navSalesPrice.getVariantCode(), navSalesPrice.getUnitOfMeasureCode(),
				navSalesPrice.getMinimumQuantity());
		dto.setExternalId(externalId);
		dto.setItemNo(navSalesPrice.getItemNo());
		dto.setSalesType(navSalesPrice.getSalesType());
		dto.setSalesCode(navSalesPrice.getSalesCode());
		if (navSalesPrice.getUnitPrice() != null) {
			dto.setUnitPrice(java.math.BigDecimal.valueOf(navSalesPrice.getUnitPrice()));
		}
		dto.setPriceIncludesVat(navSalesPrice.getPriceIncludesVat());
		dto.setResponsibilityCenter(navSalesPrice.getResponsibilityCenter());
		dto.setResponsibilityCenterType(navSalesPrice.getResponsibilityCenterType());
		dto.setStartingDate(navSalesPrice.getStartingDate());
		dto.setEndingDate(navSalesPrice.getEndingDate());
		dto.setCurrencyCode(navSalesPrice.getCurrencyCode());
		dto.setVariantCode(navSalesPrice.getVariantCode());
		dto.setUnitOfMeasureCode(navSalesPrice.getUnitOfMeasureCode());
		dto.setMinimumQuantity(navSalesPrice.getMinimumQuantity());
		dto.setLastModifiedAt(navSalesPrice.getModifiedAt());
		return dto;
	}

	public List<ErpSalesDiscountDTO> toSalesDiscountDTOs(List<DynamicsNavSalesDiscountDTO> navSalesDiscounts) {
		if (navSalesDiscounts == null) {
			return List.of();
		}
		return navSalesDiscounts.stream().map(this::toSalesDiscountDTO).toList();
	}

	public ErpSalesDiscountDTO toSalesDiscountDTO(DynamicsNavSalesDiscountDTO navSalesDiscount) {
		ErpSalesDiscountDTO dto = new ErpSalesDiscountDTO();
		// Build composite externalId: Type + Code + Sales_Type + Sales_Code +
		// Responsibility_Center + Starting_Date + AuxiliaryIndex1-4
		String externalId = buildSalesDiscountExternalId(navSalesDiscount.getType(), navSalesDiscount.getCode(),
				navSalesDiscount.getSalesType(), navSalesDiscount.getSalesCode(),
				navSalesDiscount.getResponsibilityCenter(), navSalesDiscount.getStartingDate(),
				navSalesDiscount.getAuxiliaryIndex1(), navSalesDiscount.getAuxiliaryIndex2(),
				navSalesDiscount.getAuxiliaryIndex3(), navSalesDiscount.getAuxiliaryIndex4());
		dto.setExternalId(externalId);
		dto.setType(navSalesDiscount.getType());
		dto.setCode(navSalesDiscount.getCode());
		dto.setSalesType(navSalesDiscount.getSalesType());
		dto.setSalesCode(navSalesDiscount.getSalesCode());
		dto.setResponsibilityCenterType(navSalesDiscount.getResponsibilityCenterType());
		dto.setResponsibilityCenter(navSalesDiscount.getResponsibilityCenter());
		dto.setStartingDate(navSalesDiscount.getStartingDate());
		dto.setEndingDate(navSalesDiscount.getEndingDate());
		if (navSalesDiscount.getLineDiscount() != null) {
			dto.setLineDiscount(java.math.BigDecimal.valueOf(navSalesDiscount.getLineDiscount()));
		}
		dto.setLastModifiedAt(navSalesDiscount.getModifiedAt());
		dto.setAuxiliaryIndex1(navSalesDiscount.getAuxiliaryIndex1());
		dto.setAuxiliaryIndex2(navSalesDiscount.getAuxiliaryIndex2());
		dto.setAuxiliaryIndex3(navSalesDiscount.getAuxiliaryIndex3());
		dto.setAuxiliaryIndex4(navSalesDiscount.getAuxiliaryIndex4());
		return dto;
	}

	private String buildSalesPriceExternalId(String itemNo, String salesType, String salesCode,
			String responsibilityCenter, String startingDate, String currencyCode, String variantCode,
			String unitOfMeasureCode, Double minimumQuantity) {
		return (itemNo != null ? itemNo : "") + "|" + (salesType != null ? salesType : "") + "|"
				+ (salesCode != null ? salesCode : "") + "|"
				+ (responsibilityCenter != null ? responsibilityCenter : "") + "|"
				+ (startingDate != null ? startingDate : "") + "|" + (currencyCode != null ? currencyCode : "") + "|"
				+ (variantCode != null ? variantCode : "") + "|" + (unitOfMeasureCode != null ? unitOfMeasureCode : "")
				+ "|" + (minimumQuantity != null ? minimumQuantity.toString() : "");
	}

	private String buildSalesDiscountExternalId(String type, String code, String salesType, String salesCode,
			String responsibilityCenter, String startingDate, String auxiliaryIndex1, String auxiliaryIndex2,
			String auxiliaryIndex3, Integer auxiliaryIndex4) {
		return (type != null ? type : "") + "|" + (code != null ? code : "") + "|"
				+ (salesType != null ? salesType : "") + "|" + (salesCode != null ? salesCode : "") + "|"
				+ (responsibilityCenter != null ? responsibilityCenter : "") + "|"
				+ (startingDate != null ? startingDate : "") + "|" + (auxiliaryIndex1 != null ? auxiliaryIndex1 : "")
				+ "|" + (auxiliaryIndex2 != null ? auxiliaryIndex2 : "") + "|"
				+ (auxiliaryIndex3 != null ? auxiliaryIndex3 : "") + "|"
				+ (auxiliaryIndex4 != null ? auxiliaryIndex4.toString() : "");
	}

	/**
	 * Convert ErpTicketDTO to DynamicsNavSalesOrderHeaderDTO
	 */
	public DynamicsNavSalesOrderHeaderDTO toSalesOrderHeaderDTO(ErpTicketDTO ticket) {
		DynamicsNavSalesOrderHeaderDTO dto = new DynamicsNavSalesOrderHeaderDTO();

		// Set customer information
		if (ticket.getCustomerExternalId() != null) {
			dto.setSellToCustomerNo(ticket.getCustomerExternalId());
		}

		// Set responsibility center
		if (ticket.getResponsibilityCenter() != null) {
			dto.setResponsibilityCenter(ticket.getResponsibilityCenter());
		}

		// Set location code
		if (ticket.getLocationExternalId() != null) {
			dto.setLocationCode(ticket.getLocationExternalId());
		}

		// Set posting date
		if (ticket.getSaleDate() != null) {
			dto.setPostingDate(ticket.getSaleDate().toLocalDate());
		}

		// Set Fence_No to cashier session ID
		if (ticket.getCashierSessionId() != null) {
			dto.setFenceNo(ticket.getCashierSessionId());
		}

		// Set POS document number
		if (ticket.getTicketNumber() != null) {
			dto.setPosDocumentNo(ticket.getTicketNumber());
		}

		// Set discount percentage
		if (ticket.getDiscountPercentage() != null) {
			dto.setDiscountPercent(ticket.getDiscountPercentage());
		}

		// Set TicketAmount
		if (ticket.getTotalAmount() != null) {
			dto.setTicketAmount(ticket.getTotalAmount().doubleValue());
		}

		// Set POS_Order to false initially
		dto.setPosOrder(false);

		return dto;
	}

	/**
	 * Convert ErpTicketLineDTO to DynamicsNavSalesOrderLineDTO Note: Line_No is not
	 * set as it's auto-generated by ERP
	 */
	public DynamicsNavSalesOrderLineDTO toSalesOrderLineDTO(ErpTicketLineDTO line, String documentNo) {
		DynamicsNavSalesOrderLineDTO dto = new DynamicsNavSalesOrderLineDTO();

		// Set document number
		if (documentNo != null) {
			dto.setDocumentNo(documentNo);
		}

		// Set item number
		if (line.getItemExternalId() != null) {
			dto.setNo(line.getItemExternalId());
		}

		// Set quantity
		if (line.getQuantity() != null) {
			dto.setQuantity(line.getQuantity().doubleValue());
		}

		// Set unit price
		if (line.getUnitPrice() != null) {
			dto.setUnitPrice(line.getUnitPrice().doubleValue());
		}

		// Set line discount percentage
		if (line.getDiscountPercentage() != null) {
			dto.setLineDiscountPercent(line.getDiscountPercentage().doubleValue());
		}

		// Set location code
		if (line.getLocationCode() != null) {
			dto.setLocationCode(line.getLocationCode());
		}

		// Type is read-only, so it's excluded from serialization via @JsonIgnore on
		// getter

		return dto;
	}

	/**
	 * Convert ErpReturnDTO to DynamicsNavReturnHeaderDTO
	 */
	public DynamicsNavReturnHeaderDTO toReturnHeaderDTO(ErpReturnDTO returnDTO) {
		DynamicsNavReturnHeaderDTO dto = new DynamicsNavReturnHeaderDTO();

		// Document_Type is set to "Return Order" by default in the DTO

		// Set customer number
		if (returnDTO.getCustomerExternalId() != null) {
			dto.setSellToCustomerNo(returnDTO.getCustomerExternalId());
		}

		// Set location code
		if (returnDTO.getLocationExternalId() != null) {
			dto.setLocationCode(returnDTO.getLocationExternalId());
		}

		// Set posting date
		if (returnDTO.getReturnDate() != null) {
			dto.setPostingDate(returnDTO.getReturnDate().toLocalDate());
		}

		// Set Fence_No to cashier session ID
		if (returnDTO.getCashierSessionId() != null) {
			dto.setFenceNo(returnDTO.getCashierSessionId());
		}

		// Set POS document number (return number from our POS)
		if (returnDTO.getReturnNumber() != null) {
			dto.setPosDocumentNo(returnDTO.getReturnNumber());
		}

		// Set Ticket_Amount (total return TTC - total including VAT)
		// Always calculate from lines to ensure we use TTC (total including VAT)
		if (returnDTO.getLines() != null && !returnDTO.getLines().isEmpty()) {
			// Calculate total from lines (TTC - total including VAT)
			double total = returnDTO.getLines().stream().filter(line -> line.getLineTotalIncludingVat() != null)
					.mapToDouble(line -> line.getLineTotalIncludingVat().doubleValue()).sum();
			dto.setTicketAmount(total);
		} else if (returnDTO.getTotalReturnAmount() != null) {
			// Fallback to header total if no lines available
			dto.setTicketAmount(returnDTO.getTotalReturnAmount().doubleValue());
		}

		return dto;
	}

	/**
	 * Convert ErpReturnLineDTO to DynamicsNavReturnLineDTO Note: Line_No is not set
	 * as it's auto-generated by ERP
	 */
	public DynamicsNavReturnLineDTO toReturnLineDTO(ErpReturnLineDTO line, String documentNo) {
		DynamicsNavReturnLineDTO dto = new DynamicsNavReturnLineDTO();

		// Document_Type is set to "Return Order" by default in the DTO

		// Set document number (from header erpNo)
		if (documentNo != null) {
			dto.setDocumentNo(documentNo);
		}

		// Type is set to "Item" by default in the DTO

		// Set item number
		if (line.getItemExternalId() != null) {
			dto.setNo(line.getItemExternalId());
		}

		// Set quantity
		if (line.getQuantity() != null) {
			dto.setQuantity(line.getQuantity().doubleValue());
			// Return_Qty_to_Receive = quantity (same as Quantity)
			dto.setReturnQtyToReceive(line.getQuantity().doubleValue());
		}

		// Set unit price (HT - excluding VAT)
		if (line.getUnitPrice() != null) {
			dto.setUnitPrice(line.getUnitPrice().doubleValue());
		}

		// Set line discount percentage from original sales line
		if (line.getDiscountPercentage() != null) {
			dto.setLineDiscountPercent(line.getDiscountPercentage().doubleValue());
		}

		return dto;
	}

	/**
	 * Convert ErpSessionDTO to DynamicsNavSessionDTO
	 */
	public DynamicsNavSessionDTO toSessionDTO(ErpSessionDTO sessionDTO) {
		DynamicsNavSessionDTO dto = new DynamicsNavSessionDTO();

		// Set fence_no (session number)
		if (sessionDTO.getSessionNumber() != null) {
			dto.setFenceNo(sessionDTO.getSessionNumber());
		}

		// Set location
		if (sessionDTO.getLocationCode() != null) {
			dto.setLocation(sessionDTO.getLocationCode());
		}

		// Set number of tickets
		if (sessionDTO.getTicketCount() != null) {
			dto.setNberTicket(sessionDTO.getTicketCount());
		} else {
			dto.setNberTicket(0);
		}

		// Set closing amount
		if (sessionDTO.getClosingAmount() != null) {
			dto.setClosingAmount(sessionDTO.getClosingAmount());
		} else {
			dto.setClosingAmount(0.0);
		}

		// Set number of returns cashed
		if (sessionDTO.getReturnCashedCount() != null) {
			dto.setNberReturnCashed(sessionDTO.getReturnCashedCount());
		} else {
			dto.setNberReturnCashed(0);
		}

		// Set amount of returns cashed
		if (sessionDTO.getReturnCashedAmount() != null) {
			dto.setAmountReturnCashed(sessionDTO.getReturnCashedAmount().doubleValue());
		} else {
			dto.setAmountReturnCashed(0.0);
		}

		// Set number of returns (all returns - simple + voucher)
		if (sessionDTO.getReturnCount() != null) {
			dto.setNberReturn(sessionDTO.getReturnCount());
		} else {
			dto.setNberReturn(0);
		}

		return dto;
	}
}
