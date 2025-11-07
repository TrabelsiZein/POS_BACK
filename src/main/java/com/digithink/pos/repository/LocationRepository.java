package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.Location;

public interface LocationRepository extends _BaseRepository<Location, Long> {

	Optional<Location> findByLocationCode(String locationCode);

	Optional<Location> findByName(String name);

	Optional<Location> findByIsDefaultTrue();
}

