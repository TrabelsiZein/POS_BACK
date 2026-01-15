package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.SalesPrice;
import com.digithink.pos.repository.SalesPriceRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesPriceService extends _BaseService<SalesPrice, Long> {

	@Autowired
	private SalesPriceRepository salesPriceRepository;

	@Override
	protected _BaseRepository<SalesPrice, Long> getRepository() {
		return salesPriceRepository;
	}

	/**
	 * Get SalesPriceRepository specifically (for accessing in controller)
	 */
	public SalesPriceRepository getSalesPriceRepository() {
		return salesPriceRepository;
	}
}

