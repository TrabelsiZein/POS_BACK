package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.Location;
import com.digithink.pos.repository.LocationRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class LocationService extends _BaseService<Location, Long> {

	@Autowired
	private LocationRepository locationRepository;

	@Override
	protected _BaseRepository<Location, Long> getRepository() {
		return locationRepository;
	}
}

