package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.Location;
import com.digithink.business_management.repository.LocationRepository;

@Service
public class LocationService extends _BaseService<Location, Long> {

	@Autowired
	private LocationRepository locationRepository;

	@Override
	protected JpaRepository<Location, Long> getRepository() {
		return locationRepository;
	}

	@Override
	protected JpaSpecificationExecutor<Location> getJpaSpecificationExecutor() {
		// TODO Auto-generated method stub
		return null;
	}

}
