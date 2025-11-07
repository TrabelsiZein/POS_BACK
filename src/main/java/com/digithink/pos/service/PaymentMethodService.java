package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.repository.PaymentMethodRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class PaymentMethodService extends _BaseService<PaymentMethod, Long> {

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;

	@Override
	protected _BaseRepository<PaymentMethod, Long> getRepository() {
		return paymentMethodRepository;
	}
}

