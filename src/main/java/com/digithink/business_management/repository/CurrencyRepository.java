package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

}
