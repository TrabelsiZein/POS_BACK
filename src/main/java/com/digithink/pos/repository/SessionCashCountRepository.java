package com.digithink.pos.repository;

import java.util.List;

import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.SessionCashCount;
import com.digithink.pos.model.enumeration.CounterType;

public interface SessionCashCountRepository extends _BaseRepository<SessionCashCount, Long> {

	List<SessionCashCount> findByCashierSession(CashierSession cashierSession);

	List<SessionCashCount> findByCashierSessionAndCounterType(CashierSession cashierSession, CounterType counterType);
}

