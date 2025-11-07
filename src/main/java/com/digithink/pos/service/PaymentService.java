package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.Payment;
import com.digithink.pos.repository.PaymentRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class PaymentService extends _BaseService<Payment, Long> {

	@Autowired
	private PaymentRepository paymentRepository;

	@Override
	protected _BaseRepository<Payment, Long> getRepository() {
		return paymentRepository;
	}
}

