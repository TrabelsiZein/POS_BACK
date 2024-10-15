package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.SalesSetup;
import com.digithink.business_management.repository.SalesSetupRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class SalesSetupService extends _BaseService<SalesSetup, Long> {

	@Autowired
	private SalesSetupRepository salesSetupRepository;

	@Override
	protected _BaseRepository<SalesSetup, Long> getRepository() {
		return salesSetupRepository;
	}

}
