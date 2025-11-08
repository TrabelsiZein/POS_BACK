package com.digithink.pos.erp.dynamicsnav.client;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.digithink.pos.erp.dynamicsnav.config.DynamicsNavProperties;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavCollectionResponse;
import com.digithink.pos.erp.dynamicsnav.dto.DynamicsNavFamilyDTO;
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
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavFamilyDTO>> response =
					dynamicsNavRestTemplate.exchange(
							url,
							HttpMethod.GET,
							null,
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
			ResponseEntity<DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO>> response =
					dynamicsNavRestTemplate.exchange(
							url,
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO>>() {
							});

			DynamicsNavCollectionResponse<DynamicsNavSubFamilyDTO> body = response.getBody();
			return body != null && body.getValue() != null ? body.getValue() : Collections.emptyList();
		} catch (Exception ex) {
			LOGGER.error("Failed to fetch item subfamilies from Dynamics NAV: {}", ex.getMessage(), ex);
			return Collections.emptyList();
		}
	}

	private String buildCompanyEndpointUrl(String entityName) {
		String companySegment = properties.getCompanyUrlSegment();
		String path = (companySegment.isEmpty() ? "" : "/" + companySegment) + "/" + entityName;

		return UriComponentsBuilder.fromHttpUrl(ensureTrailingSlash(properties.getBaseUrl()))
				.path(path)
				.build(false)
				.toUriString();
	}

	private String ensureTrailingSlash(String baseUrl) {
		if (baseUrl == null || baseUrl.isEmpty()) {
			return "";
		}
		return baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
	}
}

