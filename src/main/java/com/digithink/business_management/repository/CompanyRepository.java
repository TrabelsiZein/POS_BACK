package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
