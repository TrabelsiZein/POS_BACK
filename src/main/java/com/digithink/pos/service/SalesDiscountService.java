package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.SalesDiscount;
import com.digithink.pos.repository.SalesDiscountRepository;
import com.digithink.pos.repository._BaseRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SalesDiscountService extends _BaseService<SalesDiscount, Long> {

	@Autowired
	private SalesDiscountRepository salesDiscountRepository;

	@Override
	protected _BaseRepository<SalesDiscount, Long> getRepository() {
		return salesDiscountRepository;
	}

	/**
	 * Get SalesDiscountRepository specifically (for accessing in controller)
	 */
	public SalesDiscountRepository getSalesDiscountRepository() {
		return salesDiscountRepository;
	}
}

