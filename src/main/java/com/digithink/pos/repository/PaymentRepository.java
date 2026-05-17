package com.digithink.pos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digithink.pos.model.CashierSession;
import com.digithink.pos.model.Payment;
import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.SalesHeader;
import com.digithink.pos.model.enumeration.TransactionStatus;

public interface PaymentRepository extends _BaseRepository<Payment, Long> {

	List<Payment> findByStatus(TransactionStatus status);

	List<Payment> findBySalesHeader(SalesHeader salesHeader);

	List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

	@Query("SELECT p FROM Payment p JOIN p.salesHeader sh WHERE sh.cashierSession = :session AND sh.status NOT IN :excludedStatuses")
	List<Payment> findByCashierSession(@Param("session") CashierSession session, @Param("excludedStatuses") List<TransactionStatus> excludedStatuses);
}
