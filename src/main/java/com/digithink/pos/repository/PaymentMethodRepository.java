package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.PaymentMethod;
import com.digithink.pos.model.enumeration.PaymentMethodType;

public interface PaymentMethodRepository extends _BaseRepository<PaymentMethod, Long> {

	Optional<PaymentMethod> findByCode(String code);

	Optional<PaymentMethod> findByType(PaymentMethodType methodType);

}
