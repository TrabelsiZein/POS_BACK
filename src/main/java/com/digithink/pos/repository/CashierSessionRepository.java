package com.digithink.pos.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.UserAccount;
import com.digithink.pos.model.enumeration.SessionStatus;
import com.digithink.pos.model.enumeration.SynchronizationStatus;

public interface CashierSessionRepository extends _BaseRepository<CashierSession, Long> {

	Optional<CashierSession> findBySessionNumber(String sessionNumber);

	List<CashierSession> findByCashier(UserAccount cashier);

	List<CashierSession> findByStatus(SessionStatus status);

	Optional<CashierSession> findByCashierAndStatus(UserAccount cashier, SessionStatus status);

	List<CashierSession> findByVerifiedBy(UserAccount user);
	
	long countByOpenedAtGreaterThanEqual(LocalDateTime date);

	// Query methods for synchronization
	// Find sessions that are CLOSED or TERMINATED and not totally synched
	List<CashierSession> findByStatusInAndSynchronizationStatusNot(
			java.util.List<SessionStatus> statuses, SynchronizationStatus synchronizationStatus);
}

