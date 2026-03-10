package com.digithink.pos.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.digithink.pos.dto.ImportFieldMappingDTO;
import com.digithink.pos.dto.ImportPreviewDTO;
import com.digithink.pos.dto.ImportResultDTO;
import com.digithink.pos.dto.ImportResultDTO.RowError;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.Item;
import com.digithink.pos.model.ItemFamily;
import com.digithink.pos.model.ItemSubFamily;
import com.digithink.pos.model.Vendor;
import com.digithink.pos.repository.CustomerRepository;
import com.digithink.pos.repository.ItemFamilyRepository;
import com.digithink.pos.repository.ItemRepository;
import com.digithink.pos.repository.ItemSubFamilyRepository;
import com.digithink.pos.repository.VendorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class DataImportService {

    private static final int PREVIEW_ROW_LIMIT = 5;

    private final ItemFamilyRepository itemFamilyRepository;
    private final ItemSubFamilyRepository itemSubFamilyRepository;
    private final ItemRepository itemRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;

    public ImportPreviewDTO previewFile(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return new ImportPreviewDTO(new ArrayList<>(), new ArrayList<>(), 0);
            }

            List<String> columns = new ArrayList<>();
            for (Cell cell : headerRow) {
                String value = formatter.formatCellValue(cell).trim();
                if (!value.isEmpty()) {
                    columns.add(value);
                }
            }

            int totalRows = 0;
            List<List<String>> previewRows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;
                if (previewRows.size() < PREVIEW_ROW_LIMIT) {
                    List<String> rowData = new ArrayList<>();
                    for (int j = 0; j < columns.size(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        rowData.add(cell != null ? formatter.formatCellValue(cell).trim() : "");
                    }
                    previewRows.add(rowData);
                }
            }

            return new ImportPreviewDTO(columns, previewRows, totalRows);
        }
    }

    @Transactional
    public ImportResultDTO executeImport(MultipartFile file, String entityType,
                                         List<ImportFieldMappingDTO> mapping) throws IOException {
        switch (entityType.toUpperCase()) {
            case "FAMILIES":    return importFamilies(file, mapping);
            case "SUBFAMILIES": return importSubFamilies(file, mapping);
            case "ITEMS":       return importItems(file, mapping);
            case "VENDORS":     return importVendors(file, mapping);
            case "CUSTOMERS":   return importCustomers(file, mapping);
            default:
                throw new IllegalArgumentException("Unknown entity type: " + entityType);
        }
    }

    // ─── Families ────────────────────────────────────────────────────────────

    private ImportResultDTO importFamilies(MultipartFile file, List<ImportFieldMappingDTO> mapping)
            throws IOException {
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> colIndex = buildColumnIndex(sheet, formatter);
            Map<String, String> fieldMap = buildFieldMap(mapping);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;

                try {
                    String code = getRequiredValue(row, formatter, colIndex, fieldMap, "code", i);
                    String name = getRequiredValue(row, formatter, colIndex, fieldMap, "name", i);

                    ItemFamily family = itemFamilyRepository.findByCode(code).orElse(new ItemFamily());
                    family.setCode(code);
                    family.setName(name);
                    family.setDescription(getOptionalValue(row, formatter, colIndex, fieldMap, "description"));
                    String displayOrderStr = getOptionalValue(row, formatter, colIndex, fieldMap, "displayOrder");
                    if (displayOrderStr != null && !displayOrderStr.isEmpty()) {
                        family.setDisplayOrder(parseInteger(displayOrderStr));
                    }
                    if (family.getId() == null) {
                        family.setCreatedAt(LocalDateTime.now());
                        family.setCreatedBy("Import");
                    }
                    family.setUpdatedAt(LocalDateTime.now());
                    family.setUpdatedBy("Import");
                    itemFamilyRepository.save(family);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new RowError(i + 1, e.getMessage()));
                    log.warn("Family import row {} error: {}", i + 1, e.getMessage());
                }
            }
        }
        return new ImportResultDTO(totalRows, successCount, errors.size(), errors);
    }

    // ─── Subfamilies ──────────────────────────────────────────────────────────

    private ImportResultDTO importSubFamilies(MultipartFile file, List<ImportFieldMappingDTO> mapping)
            throws IOException {
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> colIndex = buildColumnIndex(sheet, formatter);
            Map<String, String> fieldMap = buildFieldMap(mapping);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;

                try {
                    String code = getRequiredValue(row, formatter, colIndex, fieldMap, "code", i);
                    String name = getRequiredValue(row, formatter, colIndex, fieldMap, "name", i);
                    String familyCode = getRequiredValue(row, formatter, colIndex, fieldMap, "familyCode", i);

                    ItemFamily family = itemFamilyRepository.findByCode(familyCode)
                            .orElseThrow(() -> new RuntimeException("Family not found: " + familyCode));

                    ItemSubFamily subFamily = itemSubFamilyRepository.findByCode(code).orElse(new ItemSubFamily());
                    subFamily.setCode(code);
                    subFamily.setName(name);
                    subFamily.setItemFamily(family);
                    subFamily.setDescription(getOptionalValue(row, formatter, colIndex, fieldMap, "description"));
                    String displayOrderStr = getOptionalValue(row, formatter, colIndex, fieldMap, "displayOrder");
                    if (displayOrderStr != null && !displayOrderStr.isEmpty()) {
                        subFamily.setDisplayOrder(parseInteger(displayOrderStr));
                    }
                    if (subFamily.getId() == null) {
                        subFamily.setCreatedAt(LocalDateTime.now());
                        subFamily.setCreatedBy("Import");
                    }
                    subFamily.setUpdatedAt(LocalDateTime.now());
                    subFamily.setUpdatedBy("Import");
                    itemSubFamilyRepository.save(subFamily);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new RowError(i + 1, e.getMessage()));
                    log.warn("SubFamily import row {} error: {}", i + 1, e.getMessage());
                }
            }
        }
        return new ImportResultDTO(totalRows, successCount, errors.size(), errors);
    }

    // ─── Items ────────────────────────────────────────────────────────────────

    private ImportResultDTO importItems(MultipartFile file, List<ImportFieldMappingDTO> mapping)
            throws IOException {
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> colIndex = buildColumnIndex(sheet, formatter);
            Map<String, String> fieldMap = buildFieldMap(mapping);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;

                try {
                    String itemCode = getRequiredValue(row, formatter, colIndex, fieldMap, "itemCode", i);
                    String name = getRequiredValue(row, formatter, colIndex, fieldMap, "name", i);
                    String unitPriceStr = getRequiredValue(row, formatter, colIndex, fieldMap, "unitPrice", i);

                    Item item = itemRepository.findByItemCode(itemCode).orElse(new Item());
                    item.setItemCode(itemCode);
                    item.setName(name);
                    item.setUnitPrice(parseDouble(unitPriceStr));
                    item.setDescription(getOptionalValue(row, formatter, colIndex, fieldMap, "description"));

                    String vatStr = getOptionalValue(row, formatter, colIndex, fieldMap, "defaultVAT");
                    if (vatStr != null && !vatStr.isEmpty()) {
                        item.setDefaultVAT(parseInteger(vatStr));
                    }
                    String costStr = getOptionalValue(row, formatter, colIndex, fieldMap, "costPrice");
                    if (costStr != null && !costStr.isEmpty()) {
                        item.setCostPrice(parseDouble(costStr));
                    }
                    String stockStr = getOptionalValue(row, formatter, colIndex, fieldMap, "stockQuantity");
                    if (stockStr != null && !stockStr.isEmpty()) {
                        item.setStockQuantity(parseInteger(stockStr));
                    }
                    String minStockStr = getOptionalValue(row, formatter, colIndex, fieldMap, "minStockLevel");
                    if (minStockStr != null && !minStockStr.isEmpty()) {
                        item.setMinStockLevel(parseInteger(minStockStr));
                    }
                    item.setBarcode(getOptionalValue(row, formatter, colIndex, fieldMap, "barcode"));
                    item.setUnitOfMeasure(getOptionalValue(row, formatter, colIndex, fieldMap, "unitOfMeasure"));
                    item.setCategory(getOptionalValue(row, formatter, colIndex, fieldMap, "category"));
                    item.setBrand(getOptionalValue(row, formatter, colIndex, fieldMap, "brand"));

                    String familyCode = getOptionalValue(row, formatter, colIndex, fieldMap, "familyCode");
                    if (familyCode != null && !familyCode.isEmpty()) {
                        Optional<ItemFamily> family = itemFamilyRepository.findByCode(familyCode);
                        if (family.isPresent()) {
                            item.setItemFamily(family.get());
                        } else {
                            log.warn("Item row {}: family code '{}' not found, skipping family assignment", i + 1, familyCode);
                        }
                    }
                    String subFamilyCode = getOptionalValue(row, formatter, colIndex, fieldMap, "subFamilyCode");
                    if (subFamilyCode != null && !subFamilyCode.isEmpty()) {
                        Optional<ItemSubFamily> subFamily = itemSubFamilyRepository.findByCode(subFamilyCode);
                        if (subFamily.isPresent()) {
                            item.setItemSubFamily(subFamily.get());
                        } else {
                            log.warn("Item row {}: subfamily code '{}' not found, skipping subfamily assignment", i + 1, subFamilyCode);
                        }
                    }

                    if (item.getId() == null) {
                        item.setCreatedAt(LocalDateTime.now());
                        item.setCreatedBy("Import");
                        item.setShowInPos(true);
                    }
                    item.setUpdatedAt(LocalDateTime.now());
                    item.setUpdatedBy("Import");
                    itemRepository.save(item);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new RowError(i + 1, e.getMessage()));
                    log.warn("Item import row {} error: {}", i + 1, e.getMessage());
                }
            }
        }
        return new ImportResultDTO(totalRows, successCount, errors.size(), errors);
    }

    // ─── Vendors ──────────────────────────────────────────────────────────────

    private ImportResultDTO importVendors(MultipartFile file, List<ImportFieldMappingDTO> mapping)
            throws IOException {
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> colIndex = buildColumnIndex(sheet, formatter);
            Map<String, String> fieldMap = buildFieldMap(mapping);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;

                try {
                    String vendorCode = getRequiredValue(row, formatter, colIndex, fieldMap, "vendorCode", i);
                    String name = getRequiredValue(row, formatter, colIndex, fieldMap, "name", i);
                    String phone = getRequiredValue(row, formatter, colIndex, fieldMap, "phone", i);

                    Vendor vendor = vendorRepository.findByVendorCode(vendorCode).orElse(new Vendor());
                    vendor.setVendorCode(vendorCode);
                    vendor.setName(name);
                    vendor.setPhone(phone);
                    vendor.setEmail(getOptionalValue(row, formatter, colIndex, fieldMap, "email"));
                    vendor.setAddress(getOptionalValue(row, formatter, colIndex, fieldMap, "address"));
                    vendor.setCity(getOptionalValue(row, formatter, colIndex, fieldMap, "city"));
                    vendor.setCountry(getOptionalValue(row, formatter, colIndex, fieldMap, "country"));
                    vendor.setTaxId(getOptionalValue(row, formatter, colIndex, fieldMap, "taxId"));
                    vendor.setNotes(getOptionalValue(row, formatter, colIndex, fieldMap, "notes"));

                    if (vendor.getId() == null) {
                        vendor.setCreatedAt(LocalDateTime.now());
                        vendor.setCreatedBy("Import");
                    }
                    vendor.setUpdatedAt(LocalDateTime.now());
                    vendor.setUpdatedBy("Import");
                    vendorRepository.save(vendor);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new RowError(i + 1, e.getMessage()));
                    log.warn("Vendor import row {} error: {}", i + 1, e.getMessage());
                }
            }
        }
        return new ImportResultDTO(totalRows, successCount, errors.size(), errors);
    }

    // ─── Customers ────────────────────────────────────────────────────────────

    private ImportResultDTO importCustomers(MultipartFile file, List<ImportFieldMappingDTO> mapping)
            throws IOException {
        List<RowError> errors = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            Map<String, Integer> colIndex = buildColumnIndex(sheet, formatter);
            Map<String, String> fieldMap = buildFieldMap(mapping);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter)) continue;
                totalRows++;

                try {
                    String customerCode = getRequiredValue(row, formatter, colIndex, fieldMap, "customerCode", i);
                    String name = getRequiredValue(row, formatter, colIndex, fieldMap, "name", i);
                    String phone = getRequiredValue(row, formatter, colIndex, fieldMap, "phone", i);

                    Customer customer = customerRepository.findByCustomerCode(customerCode).orElse(new Customer());
                    customer.setCustomerCode(customerCode);
                    customer.setName(name);
                    customer.setPhone(phone);
                    customer.setEmail(getOptionalValue(row, formatter, colIndex, fieldMap, "email"));
                    customer.setAddress(getOptionalValue(row, formatter, colIndex, fieldMap, "address"));
                    customer.setCity(getOptionalValue(row, formatter, colIndex, fieldMap, "city"));
                    customer.setCountry(getOptionalValue(row, formatter, colIndex, fieldMap, "country"));
                    customer.setTaxId(getOptionalValue(row, formatter, colIndex, fieldMap, "taxId"));
                    customer.setNotes(getOptionalValue(row, formatter, colIndex, fieldMap, "notes"));
                    String creditStr = getOptionalValue(row, formatter, colIndex, fieldMap, "creditLimit");
                    if (creditStr != null && !creditStr.isEmpty()) {
                        customer.setCreditLimit(parseDouble(creditStr));
                    }

                    if (customer.getId() == null) {
                        customer.setCreatedAt(LocalDateTime.now());
                        customer.setCreatedBy("Import");
                        customer.setIsDefault(false);
                    }
                    customer.setUpdatedAt(LocalDateTime.now());
                    customer.setUpdatedBy("Import");
                    customerRepository.save(customer);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new RowError(i + 1, e.getMessage()));
                    log.warn("Customer import row {} error: {}", i + 1, e.getMessage());
                }
            }
        }
        return new ImportResultDTO(totalRows, successCount, errors.size(), errors);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Integer> buildColumnIndex(Sheet sheet, DataFormatter formatter) {
        Map<String, Integer> index = new HashMap<>();
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return index;
        for (Cell cell : headerRow) {
            String name = formatter.formatCellValue(cell).trim();
            if (!name.isEmpty()) {
                index.put(name, cell.getColumnIndex());
            }
        }
        return index;
    }

    private Map<String, String> buildFieldMap(List<ImportFieldMappingDTO> mapping) {
        Map<String, String> map = new HashMap<>();
        for (ImportFieldMappingDTO m : mapping) {
            if (m.getExcelColumn() != null && !m.getExcelColumn().isEmpty()) {
                map.put(m.getDbField(), m.getExcelColumn());
            }
        }
        return map;
    }

    private String getCellValue(Row row, DataFormatter formatter, Map<String, Integer> colIndex,
                                 Map<String, String> fieldMap, String dbField) {
        String excelCol = fieldMap.get(dbField);
        if (excelCol == null) return null;
        Integer idx = colIndex.get(excelCol);
        if (idx == null) return null;
        Cell cell = row.getCell(idx, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            if (val == Math.floor(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);
        }
        return formatter.formatCellValue(cell).trim();
    }

    private String getRequiredValue(Row row, DataFormatter formatter, Map<String, Integer> colIndex,
                                     Map<String, String> fieldMap, String dbField, int rowNum) {
        String value = getCellValue(row, formatter, colIndex, fieldMap, dbField);
        if (value == null || value.isEmpty()) {
            throw new RuntimeException("Required field '" + dbField + "' is missing or empty at row " + (rowNum + 1));
        }
        return value;
    }

    private String getOptionalValue(Row row, DataFormatter formatter, Map<String, Integer> colIndex,
                                     Map<String, String> fieldMap, String dbField) {
        String value = getCellValue(row, formatter, colIndex, fieldMap, dbField);
        return (value == null || value.isEmpty()) ? null : value;
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        for (Cell cell : row) {
            if (!formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid number format: '" + value + "'");
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value.replace(",", "").replace(".", "").trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid integer format: '" + value + "'");
        }
    }
}
