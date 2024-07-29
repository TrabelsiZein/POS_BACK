package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.Currency;
import com.digithink.business_management.repository.CurrencyRepository;

@Service
public class CurrencyService extends _BaseService<Currency, Long> {

	@Autowired
	private CurrencyRepository currencyRepository;

	@Override
	protected JpaRepository<Currency, Long> getRepository() {
		return currencyRepository;
	}

}
