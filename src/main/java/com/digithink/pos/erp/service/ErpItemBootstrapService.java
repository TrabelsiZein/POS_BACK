package com.digithink.pos.erp.service;

import java.math.BigDecimal;
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

import com.digithink.pos.erp.dto.ErpCustomerDTO;
import com.digithink.pos.erp.dto.ErpItemBarcodeDTO;
import com.digithink.pos.erp.dto.ErpItemDTO;
import com.digithink.pos.erp.dto.ErpItemFamilyDTO;
import com.digithink.pos.erp.dto.ErpItemSubFamilyDTO;
import com.digithink.pos.erp.dto.ErpLocationDTO;
import com.digithink.pos.erp.enumeration.ErpCommunicationStatus;
import com.digithink.pos.erp.enumeration.ErpSyncOperation;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemBarcode;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.model.Location;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository.ItemBarcodeRepository;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.ItemSubFamilyRepository;
import com.digithink.pos.repository.LocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErpItemBootstrapService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErpItemBootstrapService.class);
	private static final String SYSTEM_USER = "ERP_SYNC";

	private final ItemFamilyRepository itemFamilyRepository;
	private final ItemSubFamilyRepository itemSubFamilyRepository;
	private final ItemRepository itemRepository;
	private final ItemBarcodeRepository itemBarcodeRepository;
	private final CustomerRepository customerRepository;
	private final LocationRepository locationRepository;
	private final ErpCommunicationService communicationService;

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
				String message = "Skipping item family import because no code/external identifier provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_ITEM_FAMILIES, dto,
						resolveIdentifier(dto.getExternalId(), dto.getCode()), message);
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

			Optional<ItemFamily> familyOpt = resolveFamily(dto.getFamilyExternalId());
			if (!familyOpt.isPresent()) {
				String message = String.format("Skipping item subfamily import because parent family %s not found",
						dto.getFamilyExternalId());
				LOGGER.warn("{}: {}", message, dto.getExternalId());
				recordWarning(ErpSyncOperation.IMPORT_ITEM_SUBFAMILIES, dto,
						resolveIdentifier(dto.getExternalId(), dto.getCode()), message);
				continue;
			}

			ItemSubFamily entity = resolveItemSubFamily(dto);
			boolean isNew = entity.getId() == null;
			applySubFamilyValues(entity, dto, familyOpt.get());
			if (!StringUtils.hasText(entity.getCode())) {
				String message = "Skipping item subfamily import because no code/external identifier provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_ITEM_SUBFAMILIES, dto,
						resolveIdentifier(dto.getExternalId(), dto.getCode()), message);
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

	@Transactional
	public List<Item> importItems(List<ErpItemDTO> items) {
		if (items == null || items.isEmpty()) {
			return Collections.emptyList();
		}

		List<Item> persisted = new ArrayList<>();
		for (ErpItemDTO dto : items) {
			if (dto == null) {
				continue;
			}
			Item entity = resolveItem(dto);
			boolean isNew = entity.getId() == null;
			applyItemValues(entity, dto);
			if (!StringUtils.hasText(entity.getItemCode())) {
				String message = "Skipping item import because no code/external identifier provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_ITEMS, dto, resolveIdentifier(dto.getExternalId(), dto.getCode()),
						message);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(itemRepository.save(entity));
		}
		return persisted;
	}

	@Transactional
	public List<ItemBarcode> importItemBarcodes(List<ErpItemBarcodeDTO> barcodes) {
		if (barcodes == null || barcodes.isEmpty()) {
			return Collections.emptyList();
		}

		List<ItemBarcode> persisted = new ArrayList<>();
		for (ErpItemBarcodeDTO dto : barcodes) {
			if (dto == null) {
				continue;
			}
			Optional<Item> itemOpt = resolveItem(dto.getItemExternalId());
			if (!itemOpt.isPresent()) {
				String message = String.format("Skipping item barcode because parent item %s not found",
						dto.getItemExternalId());
				LOGGER.warn("{}: {}", message, dto.getBarcode());
				recordWarning(ErpSyncOperation.IMPORT_ITEM_BARCODES, dto,
						resolveIdentifier(dto.getExternalId(), dto.getBarcode()), message);
				continue;
			}

			ItemBarcode entity = resolveItemBarcode(dto);
			boolean isNew = entity.getId() == null;
			applyBarcodeValues(entity, dto, itemOpt.get());
			if (!StringUtils.hasText(entity.getBarcode())) {
				String message = "Skipping item barcode import because no barcode value provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_ITEM_BARCODES, dto,
						resolveIdentifier(dto.getExternalId(), dto.getBarcode()), message);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(itemBarcodeRepository.save(entity));
		}
		return persisted;
	}

	@Transactional
	public List<Location> importLocations(List<ErpLocationDTO> locations) {
		if (locations == null || locations.isEmpty()) {
			return Collections.emptyList();
		}

		List<Location> persisted = new ArrayList<>();
		for (ErpLocationDTO dto : locations) {
			if (dto == null) {
				continue;
			}
			Location entity = resolveLocation(dto);
			boolean isNew = entity.getId() == null;
			applyLocationValues(entity, dto);
			if (!StringUtils.hasText(entity.getLocationCode())) {
				String message = "Skipping location import because no code/external identifier provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_LOCATIONS, dto,
						resolveIdentifier(dto.getExternalId(), dto.getCode()), message);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(locationRepository.save(entity));
		}
		return persisted;
	}

	@Transactional
	public List<Customer> importCustomers(List<ErpCustomerDTO> customers) {
		if (customers == null || customers.isEmpty()) {
			return Collections.emptyList();
		}

		List<Customer> persisted = new ArrayList<>();
		for (ErpCustomerDTO dto : customers) {
			if (dto == null) {
				continue;
			}
			Customer entity = resolveCustomer(dto);
			boolean isNew = entity.getId() == null;
			applyCustomerValues(entity, dto);
			if (!StringUtils.hasText(entity.getCustomerCode())) {
				String message = "Skipping customer import because no code/external identifier provided";
				LOGGER.warn("{}: {}", message, dto);
				recordWarning(ErpSyncOperation.IMPORT_CUSTOMERS, dto,
						resolveIdentifier(dto.getExternalId(), dto.getCode()), message);
				continue;
			}
			if (isNew) {
				entity.setCreatedBy(SYSTEM_USER);
				entity.setCreatedAt(LocalDateTime.now());
			}
			entity.setUpdatedBy(SYSTEM_USER);
			entity.setUpdatedAt(LocalDateTime.now());
			persisted.add(customerRepository.save(entity));
		}
		return persisted;
	}

	private ItemFamily resolveItemFamily(ErpItemFamilyDTO dto) {
		return findFamilyByExternalOrCode(dto.getExternalId(), dto.getCode()).orElseGet(ItemFamily::new);
	}

	private Optional<ItemFamily> resolveFamily(String identifier) {
		if (!StringUtils.hasText(identifier)) {
			return Optional.empty();
		}
		Optional<ItemFamily> byExternal = itemFamilyRepository.findByErpExternalId(identifier);
		if (byExternal.isPresent()) {
			return byExternal;
		}
		return itemFamilyRepository.findByCode(identifier);
	}

	private Optional<ItemSubFamily> resolveSubFamily(String identifier) {
		if (!StringUtils.hasText(identifier)) {
			return Optional.empty();
		}
		Optional<ItemSubFamily> byExternal = itemSubFamilyRepository.findByErpExternalId(identifier);
		if (byExternal.isPresent()) {
			return byExternal;
		}
		return itemSubFamilyRepository.findByCode(identifier);
	}

	private Optional<ItemFamily> findFamilyByExternalOrCode(String externalId, String code) {
		if (StringUtils.hasText(externalId)) {
			Optional<ItemFamily> byExternalId = itemFamilyRepository.findByErpExternalId(externalId);
			if (byExternalId.isPresent()) {
				return byExternalId;
			}
		}
		if (StringUtils.hasText(code)) {
			return itemFamilyRepository.findByCode(code);
		}
		return Optional.empty();
	}

	private ItemSubFamily resolveItemSubFamily(ErpItemSubFamilyDTO dto) {
		return findSubFamilyByExternalOrCode(dto.getExternalId(), dto.getCode()).orElseGet(ItemSubFamily::new);
	}

	private Optional<ItemSubFamily> findSubFamilyByExternalOrCode(String externalId, String code) {
		if (StringUtils.hasText(externalId)) {
			Optional<ItemSubFamily> byExternalId = itemSubFamilyRepository.findByErpExternalId(externalId);
			if (byExternalId.isPresent()) {
				return byExternalId;
			}
		}
		if (StringUtils.hasText(code)) {
			return itemSubFamilyRepository.findByCode(code);
		}
		return Optional.empty();
	}

	private Item resolveItem(ErpItemDTO dto) {
		return findItemByExternalOrCode(dto.getExternalId(), dto.getCode()).orElseGet(Item::new);
	}

	private Optional<Item> resolveItem(String identifier) {
		if (!StringUtils.hasText(identifier)) {
			return Optional.empty();
		}
		Optional<Item> byExternal = itemRepository.findByErpExternalId(identifier);
		if (byExternal.isPresent()) {
			return byExternal;
		}
		return itemRepository.findByItemCode(identifier);
	}

	private Optional<Item> findItemByExternalOrCode(String externalId, String code) {
		if (StringUtils.hasText(externalId)) {
			Optional<Item> byExternalId = itemRepository.findByErpExternalId(externalId);
			if (byExternalId.isPresent()) {
				return byExternalId;
			}
		}
		if (StringUtils.hasText(code)) {
			return itemRepository.findByItemCode(code);
		}
		return Optional.empty();
	}

	private ItemBarcode resolveItemBarcode(ErpItemBarcodeDTO dto) {
		return findBarcodeByExternalOrValue(dto.getExternalId(), dto.getBarcode()).orElseGet(ItemBarcode::new);
	}

	private Optional<ItemBarcode> findBarcodeByExternalOrValue(String externalId, String barcode) {
		if (StringUtils.hasText(externalId)) {
			Optional<ItemBarcode> byExternal = itemBarcodeRepository.findByErpExternalId(externalId);
			if (byExternal.isPresent()) {
				return byExternal;
			}
		}
		if (StringUtils.hasText(barcode)) {
			return itemBarcodeRepository.findByBarcode(barcode);
		}
		return Optional.empty();
	}

	private Location resolveLocation(ErpLocationDTO dto) {
		return findLocationByExternalOrCode(dto.getExternalId(), dto.getCode()).orElseGet(Location::new);
	}

	private Optional<Location> findLocationByExternalOrCode(String externalId, String code) {
		if (StringUtils.hasText(externalId)) {
			Optional<Location> byExternal = locationRepository.findByErpExternalId(externalId);
			if (byExternal.isPresent()) {
				return byExternal;
			}
		}
		if (StringUtils.hasText(code)) {
			return locationRepository.findByLocationCode(code);
		}
		return Optional.empty();
	}

	private Customer resolveCustomer(ErpCustomerDTO dto) {
		return findCustomerByExternalOrCode(dto.getExternalId(), dto.getCode()).orElseGet(Customer::new);
	}

	private Optional<Customer> findCustomerByExternalOrCode(String externalId, String code) {
		if (StringUtils.hasText(externalId)) {
			Optional<Customer> byExternal = customerRepository.findByErpExternalId(externalId);
			if (byExternal.isPresent()) {
				return byExternal;
			}
		}
		if (StringUtils.hasText(code)) {
			return customerRepository.findByCustomerCode(code);
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

	private void applyItemValues(Item entity, ErpItemDTO dto) {
		entity.setErpExternalId(defaultString(dto.getExternalId()));
		entity.setItemCode(defaultString(dto.getCode(), dto.getExternalId()));
		entity.setName(defaultString(dto.getName(), dto.getCode(), dto.getExternalId()));
		entity.setDescription(defaultString(dto.getDescription()));
		entity.setUnitPrice(toDouble(dto.getUnitPrice()));
		entity.setDefaultVAT(dto.getDefaultVAT());
		if (dto.getActive() != null) {
			entity.setActive(dto.getActive());
		}
		resolveFamily(dto.getFamilyExternalId()).ifPresent(entity::setItemFamily);
		resolveSubFamily(dto.getSubFamilyExternalId()).ifPresent(entity::setItemSubFamily);
	}

	private void applyBarcodeValues(ItemBarcode entity, ErpItemBarcodeDTO dto, Item item) {
		entity.setErpExternalId(defaultString(dto.getExternalId(), dto.getBarcode()));
		entity.setItem(item);
		entity.setBarcode(defaultString(dto.getBarcode(), dto.getExternalId()));
		if (dto.getPrimaryBarcode() != null) {
			entity.setIsPrimary(dto.getPrimaryBarcode());
		}
		entity.setActive(Boolean.TRUE);
	}

	private void applyLocationValues(Location entity, ErpLocationDTO dto) {
		entity.setErpExternalId(defaultString(dto.getExternalId(), dto.getCode()));
		entity.setLocationCode(defaultString(dto.getCode(), dto.getExternalId()));
		entity.setName(defaultString(dto.getName(), dto.getCode(), dto.getExternalId()));
		entity.setDescription(defaultString(dto.getAddress()));
		entity.setAddress(defaultString(dto.getAddress()));
		entity.setCity(defaultString(dto.getCity()));
		entity.setCountry(defaultString(dto.getCountry()));
		if (dto.getActive() != null) {
			entity.setActive(dto.getActive());
		}
	}

	private void applyCustomerValues(Customer entity, ErpCustomerDTO dto) {
		entity.setErpExternalId(defaultString(dto.getExternalId(), dto.getCode()));
		entity.setCustomerCode(defaultString(dto.getCode(), dto.getExternalId()));
		entity.setName(defaultString(dto.getName(), dto.getCode(), dto.getExternalId()));
		entity.setEmail(defaultString(dto.getEmail()));
		entity.setPhone(defaultString(dto.getPhone()));
		entity.setAddress(defaultString(dto.getAddress()));
		entity.setCity(defaultString(dto.getCity()));
		entity.setCountry(defaultString(dto.getCountry()));
		entity.setTaxId(defaultString(dto.getTaxNumber()));
		if (dto.getActive() != null) {
			entity.setActive(dto.getActive());
		}
	}

	private Double toDouble(BigDecimal value) {
		return value != null ? value.doubleValue() : null;
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

	private void recordWarning(ErpSyncOperation operation, Object payload, String externalReference, String message) {
		communicationService.logOperation(operation, payload, null, ErpCommunicationStatus.WARNING, externalReference,
				message, LocalDateTime.now(), LocalDateTime.now());
	}

	private String resolveIdentifier(String... identifiers) {
		for (String identifier : identifiers) {
			if (StringUtils.hasText(identifier)) {
				return identifier;
			}
		}
		return null;
	}
}
