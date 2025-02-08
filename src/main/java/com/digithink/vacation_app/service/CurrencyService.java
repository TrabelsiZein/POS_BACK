package com.digithink.vacation_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.Currency;
import com.digithink.vacation_app.repository.CurrencyRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class CurrencyService extends _BaseService<Currency, Long> {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Override
	protected _BaseRepository<Currency, Long> getRepository() {
		return currencyRepository;
	}

}
