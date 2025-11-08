package com.digithink.pos.erp.dynamicsnav.config;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(prefix = "erp.dynamicsnav", name = "enabled", havingValue = "true")
public class DynamicsNavConfig {

	@Bean("dynamicsNavRestTemplate")
	public RestTemplate dynamicsNavRestTemplate(DynamicsNavProperties properties) {
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(
				AuthScope.ANY,
				new NTCredentials(
						properties.getUsername(),
						properties.getPassword(),
						null,
						properties.getDomain()));

		HttpClientBuilder clientBuilder = HttpClientBuilder.create()
				.setDefaultCredentialsProvider(credentialsProvider);

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(clientBuilder.build());

		return new RestTemplate(requestFactory);
	}
}

