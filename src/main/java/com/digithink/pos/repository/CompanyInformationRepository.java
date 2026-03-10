package com.digithink.pos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digithink.pos.model.CompanyInformation;

@Repository
public interface CompanyInformationRepository extends JpaRepository<CompanyInformation, Long> {
}
