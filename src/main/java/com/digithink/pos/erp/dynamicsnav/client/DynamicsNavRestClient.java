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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.digithink.pos.erp.dto.ErpSyncFilter;
import com.digithink.pos.erp.dynamicsnav.config.DynamicsNavProperties;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavBarcodeDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCollectionResponse;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavLocationDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavStockKeepingUnitDTO;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavSubFamilyDTO;
@Component
@ConditionalOnProperty(prefix = "erp.dynamicsnav", name = "enabled", havingValue = "true")
public class DynamicsNavRestClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicsNavRestClient.class);

	private final RestTemplate dynamicsNavRestTemplate;
	private final DynamicsNavProperties properties;

	public DynamicsNavRestClient(
			@org.springframework.beans.factory.annotation.Qualifier("dynamicsNavRestTemplate") RestTemplate dynamicsNavRestTemplate,
			DynamicsNavProperties properties) {
		this.dynamicsNavRestTemplate = dynamicsNavRestTemplate;
		this.properties = properties;
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
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch item families from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
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
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch item subfamilies from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
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
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch locations from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
		}
	}

	public List<DynamicsNavStockKeepingUnitDTO> fetchItems() {
		return fetchItems(null);
	}

	public List<DynamicsNavStockKeepingUnitDTO> fetchItems(ErpSyncFilter filter) {
		try {
			UriComponentsBuilder builder = buildCompanyEndpointUriBuilder("StockkeepingUnitList");
			buildUpdatedAfterFilter(filter, "Modified_At").ifPresent(value -> builder.queryParam("$filter", value));
			String url = builder.build(false).toUriString();
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO>> response = dynamicsNavRestTemplate
					.exchange(url, HttpMethod.GET, null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavStockKeepingUnitDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch items from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
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
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch item barcodes from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
		}
	}

	private String buildCompanyEndpointUrl(String entityName) {
		return buildCompanyEndpointUriBuilder(entityName)
				.build(false)
				.toUriString();
	}

	private UriComponentsBuilder buildCompanyEndpointUriBuilder(String entityName) {
		String companySegment = properties.getCompanyUrlSegment();
		String path = (companySegment.isEmpty() ? "" : "/" + companySegment) + "/" + entityName;

		return UriComponentsBuilder.fromHttpUrl(ensureTrailingSlash(properties.getBaseUrl()))
				.path(path);
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
		String formatted = filter.getUpdatedAfter()
				.withOffsetSameInstant(ZoneOffset.UTC)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return Optional.of(fieldName + " gt " + formatted);
	}
}
