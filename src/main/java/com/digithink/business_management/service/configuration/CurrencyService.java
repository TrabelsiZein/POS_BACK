package com.digithink.business_management.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.configuration.Currency;
import com.digithink.business_management.repository._BaseRepository;
import com.digithink.business_management.repository.configuration.CurrencyRepository;
import com.digithink.business_management.service._BaseService;

@Service
public class CurrencyService extends _BaseService<Currency, Long> {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Override
	protected _BaseRepository<Currency, Long> getRepository() {
		return currencyRepository;
	}

}
