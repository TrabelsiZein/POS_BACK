package com.digithink.pos.erp.repository;

import java.util.List;

import com.digithink.pos.erp.model.PaymentHeader;
import com.digithink.pos.erp.model.PaymentLine;
import com.digithink.pos.repository._BaseRepository;

public interface PaymentLineRepository extends _BaseRepository<PaymentLine, Long> {

	List<PaymentLine> findByPaymentHeader(PaymentHeader paymentHeader);

	List<PaymentLine> findByPaymentHeaderAndSynched(PaymentHeader paymentHeader, Boolean synched);
}

