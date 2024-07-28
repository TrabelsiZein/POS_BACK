package com.digithink.business_management.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@PostConstruct
	public void initialize() {
		try {
			// Use ClassPathResource to read from the resources folder
			GoogleCredentials credentials = GoogleCredentials
					.fromStream(new ClassPathResource("pharmaconnect-firebase.json").getInputStream());

			// Use the static factory method to create the FirebaseOptions
			FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();

			// Check if there are no apps initialized already to avoid re-initialization
			if (FirebaseApp.getApps().isEmpty()) {
				FirebaseApp.initializeApp(options);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
