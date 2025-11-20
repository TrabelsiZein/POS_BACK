package com.digithink.pos.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.Customer;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.enumeration.SynchronizationStatus;
import com.digithink.pos.model.enumeration.TransactionStatus;

public interface SalesHeaderRepository extends _BaseRepository<SalesHeader, Long> {

	Optional<SalesHeader> findBySalesNumber(String salesNumber);

	List<SalesHeader> findByStatus(TransactionStatus status);

	List<SalesHeader> findByCustomer(Customer customer);

	List<SalesHeader> findByCashierSession(CashierSession cashierSession);

	List<SalesHeader> findByCashierSessionAndStatus(CashierSession cashierSession, TransactionStatus status);

	long countBySalesDateGreaterThanEqual(LocalDateTime date);
	
	List<SalesHeader> findBySalesDateBetween(LocalDateTime startDate, LocalDateTime endDate);
	
	List<SalesHeader> findBySalesDateBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, TransactionStatus status);
	
	List<SalesHeader> findByStatusAndSynchronizationStatusNot(TransactionStatus status, SynchronizationStatus synchronizationStatus);
}
