package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.GeneralSetup;

public interface GeneralSetupRepository extends _BaseRepository<GeneralSetup, Long> {

	Optional<GeneralSetup> findByCode(String code);
}

