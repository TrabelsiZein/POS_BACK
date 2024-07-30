package com.digithink.business_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digithink.business_management.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

}
