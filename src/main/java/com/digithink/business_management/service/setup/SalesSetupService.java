package com.digithink.business_management.service.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.setup.SalesSetup;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.setup.SalesSetupRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class SalesSetupService extends _BaseService<SalesSetup, Long> {

	@Autowired
	private SalesSetupRepository salesSetupRepository;

	@Override
	protected _BaseRepository<SalesSetup, Long> getRepository() {
		return salesSetupRepository;
	}

}
