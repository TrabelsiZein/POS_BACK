package com.digithink.base_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.base_app.model.Currency;
import com.digithink.base_app.repository.CurrencyRepository;
import com.digithink.base_app.repository._BaseRepository;

@Service
public class CurrencyService extends _BaseService<Currency, Long> {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Override
	protected _BaseRepository<Currency, Long> getRepository() {
		return currencyRepository;
	}

}
