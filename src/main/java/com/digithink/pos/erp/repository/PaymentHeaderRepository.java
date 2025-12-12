package com.digithink.pos.erp.repository;

import java.util.List;

import com.digithink.pos.model.CashierSession;
import com.digithink.pos.repository._BaseRepository;
import com.digithink.pos.erp.model.PaymentHeader;

public interface PaymentHeaderRepository extends _BaseRepository<PaymentHeader, Long> {

	List<PaymentHeader> findByCashierSession(CashierSession cashierSession);
}

