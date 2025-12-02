package com.digithink.pos.erp.dynamicsnav.client;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dynamicsnav.config.DynamicsNavProperties;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavBarcodeDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCollectionResponse;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCustomerDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderHeaderDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSalesOrderLineDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavStockKeepingUnitDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSubFamilyDTO;
import com.digithink.pos.erp.service.ErpSyncWarningException;
import com.digithink.pos.service.GeneralSetupService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
@ConditionalOnProperty(prefix = "erp.dynamicsnav", name = "enabled", havingValue = "true")
public class DynamicsNavRestClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicsNavRestClient.class);

	private final RestTemplate dynamicsNavRestTemplate;
	private final DynamicsNavProperties properties;
	private final GeneralSetupService generalSetupService;
	private final ObjectMapper objectMapper;

	public DynamicsNavRestClient(
			@org.springframework.beans.factory.annotation.Qualifier("dynamicsNavRestTemplate") RestTemplate dynamicsNavRestTemplate,
			DynamicsNavProperties properties, GeneralSetupService generalSetupService) {
		this.dynamicsNavRestTemplate = dynamicsNavRestTemplate;
		this.properties = properties;
		this.generalSetupService = generalSetupService;
		// Configure ObjectMapper to exclude null fields (like Postman does)
		// and serialize LocalDate as ISO date string (YYYY-MM-DD)
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
		this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	public List<DynamicsNavFamilyDTO> fetchItemFamilies() {
		try {
			String url = buildCompanyEndpointUrl("FamilyList");
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavFamilyDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavFamilyDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavFamilyDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch item families from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch item families: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch item families from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch item families: " + ex.getMessage(), ex);
		}
	}

	public List<DynamicsNavSubFamilyDTO> fetchItemSubFamilies() {
		try {
			String url = buildCompanyEndpointUrl("SubFamilyList");
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch item subfamilies from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch item subfamilies: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch item subfamilies from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch item subfamilies: " + ex.getMessage(), ex);
		}
	}

	public List<DynamicsNavLocationDTO> fetchLocations() {
		try {
			String url = buildCompanyEndpointUrl("LocationList");
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavLocationDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavLocationDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavLocationDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch locations from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch locations: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch locations from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch locations: " + ex.getMessage(), ex);
		}
	}

	public List<DynamicsNavStockKeepingUnitDTO> fetchItems() {
		return fetchItems(null);
	}

	public List<DynamicsNavStockKeepingUnitDTO> fetchItems(ErpSyncFilter filter) {
		try {
			String defaultLocation = resolveDefaultLocation();
			if (defaultLocation == null || defaultLocation.isBlank()) {
				throw new ErpSyncWarningException("DEFAULT_LOCATION is not configured");
			}

			UriComponentsBuilder builder = buildCompanyEndpointUriBuilder("StockkeepingUnitList");
			appendSkuFilters(builder, filter, defaultLocation);
			String url = builder.build(false).toUriString();
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (ErpSyncWarningException warning) {
			throw warning;
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch items from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch items: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch items from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch items: " + ex.getMessage(), ex);
		}
	}

	public List<DynamicsNavBarcodeDTO> fetchItemBarcodes() {
		return fetchItemBarcodes(null);
	}

	public List<DynamicsNavBarcodeDTO> fetchItemBarcodes(ErpSyncFilter filter) {
		try {
			UriComponentsBuilder builder = buildCompanyEndpointUriBuilder("BarCodeList");
			buildUpdatedAfterFilter(filter, "Modified_At").ifPresent(value -> builder.queryParam("$filter", value));
			String url = builder.build(false).toUriString();
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavBarcodeDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavBarcodeDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavBarcodeDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch item barcodes from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch item barcodes: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch item barcodes from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch item barcodes: " + ex.getMessage(), ex);
		}
	}

	public List<DynamicsNavCustomerDTO> fetchCustomers(ErpSyncFilter filter) {
		try {
			UriComponentsBuilder builder = buildCompanyEndpointUriBuilder("CustomerList");
			buildUpdatedAfterFilter(filter, "Last_Date_Modified")
					.ifPresent(value -> builder.queryParam("$filter", value));
			String url = builder.build(false).toUriString();
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavCustomerDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavCustomerDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavCustomerDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to fetch customers from Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage, ex);
			throw new RuntimeException("Failed to fetch customers: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to fetch customers from Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to fetch customers: " + ex.getMessage(), ex);
		}
	}

	private String buildCompanyEndpointUrl(String entityName) {
		return buildCompanyEndpointUriBuilder(entityName).build(false).toUriString();
	}

	private UriComponentsBuilder buildCompanyEndpointUriBuilder(String entityName) {
		String companySegment = properties.getCompanyUrlSegment();
		String path = (companySegment.isEmpty() ? "" : "/" + companySegment) + "/" + entityName;

		return UriComponentsBuilder.fromHttpUrl(ensureTrailingSlash(properties.getBaseUrl())).path(path);
	}

	private String ensureTrailingSlash(String baseUrl) {
		if (baseUrl == null || baseUrl.isEmpty()) {
			return "";
		}
		return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
	}

	private Optional<String> buildUpdatedAfterFilter(ErpSyncFilter filter, String fieldName) {
		if (filter == null || filter.getUpdatedAfter() == null) {
			return Optional.empty();
		}
		String formatted = filter.getUpdatedAfter().withOffsetSameInstant(ZoneOffset.UTC)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return Optional.of(fieldName + " gt " + formatted);
	}

	private void appendSkuFilters(UriComponentsBuilder builder, ErpSyncFilter filter, String defaultLocation) {
		Optional<String> dateFilter = buildUpdatedAfterFilter(filter, "Modified_At");
		String locationFilter = buildLocationFilter(defaultLocation);

		if (dateFilter.isPresent() && locationFilter != null) {
			builder.queryParam("$filter", dateFilter.get() + " and " + locationFilter);
		} else if (dateFilter.isPresent()) {
			builder.queryParam("$filter", dateFilter.get());
		} else if (locationFilter != null) {
			builder.queryParam("$filter", locationFilter);
		}
	}

	private String buildLocationFilter(String location) {
		if (location == null || location.isBlank()) {
			return null;
		}
		String escaped = location.replace("'", "''");
		return "Location_Code eq '" + escaped + "'";
	}

	private String resolveDefaultLocation() {
		return generalSetupService.findValueByCode("DEFAULT_LOCATION");
	}

	/**
	 * Create a sales order header in Dynamics NAV
	 */
	public DynamicsNavSalesOrderHeaderDTO createSalesOrderHeader(DynamicsNavSalesOrderHeaderDTO header) {
		try {
			String url = buildCompanyEndpointUrl("SalesOrdersPos");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DynamicsNavSalesOrderHeaderDTO> request = new HttpEntity<>(header, headers);

			ResponseEntity<DynamicsNavSalesOrderHeaderDTO> response = dynamicsNavRestTemplate.postForEntity(url,
					request, DynamicsNavSalesOrderHeaderDTO.class);

			// Extract Document_No from response (it's read-only so it will be in the
			// response)
			DynamicsNavSalesOrderHeaderDTO createdHeader = response.getBody();
			if (createdHeader != null && createdHeader.getDocumentNo() != null) {
				header.setDocumentNo(createdHeader.getDocumentNo());
			}

			return header;
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to create sales order header in Dynamics NAV: {} - {}", ex.getStatusCode(),
					errorMessage, ex);
			// Re-throw the original exception so parent can extract response body
			throw ex;
		} catch (RestClientException ex) {
			LOGGER.error("Failed to create sales order header in Dynamics NAV: {}", ex.getMessage(), ex);
			throw ex;
		}
	}

	/**
	 * Create a sales order line in Dynamics NAV
	 */
	public DynamicsNavSalesOrderLineDTO createSalesOrderLine(DynamicsNavSalesOrderLineDTO line) {
		try {
			String url = buildCompanyEndpointUrl("SalesOrdersPosSalesLines");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DynamicsNavSalesOrderLineDTO> request = new HttpEntity<>(line, headers);

			ResponseEntity<DynamicsNavSalesOrderLineDTO> response = dynamicsNavRestTemplate.postForEntity(url, request,
					DynamicsNavSalesOrderLineDTO.class);

			return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to create sales order line in Dynamics NAV: {} - {}", ex.getStatusCode(), errorMessage,
					ex);
			// Re-throw the original exception so parent can extract response body
			throw ex;
		} catch (RestClientException ex) {
			LOGGER.error("Failed to create sales order line in Dynamics NAV: {}", ex.getMessage(), ex);
			throw new RuntimeException("Failed to create sales order line: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Update POS_Order field to true in Dynamics NAV header Note: This uses PATCH
	 * method to update only the POS_Order field
	 */
	public void updateSalesOrderHeaderPosOrder(String documentNo, boolean posOrder) {
		try {
			// Build URL for specific document - escape the document number for URL
			String escapedDocNo = documentNo.replace("'", "''");
			String url = buildCompanyEndpointUrl("SalesOrdersPos") + "('" + escapedDocNo + "')";

			// Create a partial update DTO with only POS_Order field
			DynamicsNavSalesOrderHeaderDTO update = new DynamicsNavSalesOrderHeaderDTO();
			update.setPosOrder(posOrder);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DynamicsNavSalesOrderHeaderDTO> request = new HttpEntity<>(update, headers);

			// Use exchange with PATCH method since RestTemplate doesn't have patch method
			dynamicsNavRestTemplate.exchange(url, HttpMethod.PATCH, request, Void.class);
			LOGGER.info("Updated POS_Order to {} for document {}", posOrder, documentNo);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			String errorMessage = extractErrorMessage(ex);
			LOGGER.error("Failed to update POS_Order for document {}: {} - {}", documentNo, ex.getStatusCode(),
					errorMessage, ex);
			throw new RuntimeException("Failed to update POS_Order: " + ex.getStatusCode() + " " + errorMessage, ex);
		} catch (RestClientException ex) {
			LOGGER.error("Failed to update POS_Order for document {}: {}", documentNo, ex.getMessage(), ex);
			throw new RuntimeException("Failed to update POS_Order: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Extract detailed error message from HTTP exception response body
	 */
	private String extractErrorMessage(HttpStatusCodeException ex) {
		try {
			String responseBody = ex.getResponseBodyAsString();
			if (responseBody != null && !responseBody.trim().isEmpty()) {
				// Try to parse JSON error response
				JsonNode errorNode = objectMapper.readTree(responseBody);

				// Handle array of errors: [{"error": {...}}]
				if (errorNode.isArray() && errorNode.size() > 0) {
					JsonNode firstError = errorNode.get(0);
					if (firstError.has("error")) {
						JsonNode error = firstError.get("error");
						if (error.has("message")) {
							return error.get("message").asText();
						}
						if (error.has("code")) {
							return error.get("code").asText();
						}
					}
				}

				// Handle single error object: {"error": {...}}
				if (errorNode.has("error")) {
					JsonNode error = errorNode.get("error");
					if (error.has("message")) {
						return error.get("message").asText();
					}
					if (error.has("code")) {
						return error.get("code").asText();
					}
				}

				// If no structured error found, return the raw response body
				return responseBody;
			}
		} catch (Exception parseEx) {
			LOGGER.debug("Failed to parse error response body: {}", parseEx.getMessage());
		}

		// Fallback to status text or exception message
		return ex.getStatusText() != null ? ex.getStatusText() : ex.getMessage();
	}
}
