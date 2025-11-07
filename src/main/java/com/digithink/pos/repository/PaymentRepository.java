package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.Payment;
import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.enumeration.TransactionStatus;

public interface PaymentRepository extends _BaseRepository<Payment, Long> {

	List<Payment> findByStatus(TransactionStatus status);

	List<Payment> findBySalesHeader(SalesHeader salesHeader);

	List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);
}

