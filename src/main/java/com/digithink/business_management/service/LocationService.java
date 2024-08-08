package com.digithink.business_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.business_management.model.inventory.Location;
import com.digithink.business_management.repository.LocationRepository;
import com.digithink.business_management.repository._BaseRepository;

@Service
public class LocationService extends _BaseService<Location, Long> {

	@Autowired
	private LocationRepository locationRepository;

	@Override
	protected _BaseRepository<Location, Long> getRepository() {
		return locationRepository;
	}

}
