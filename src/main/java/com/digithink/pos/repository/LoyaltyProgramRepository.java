package com.digithink.pos.repository;

import java.util.List;
import java.util.Optional;

import com.digithink.pos.model.LoyaltyProgram;

public interface LoyaltyProgramRepository extends _BaseRepository<LoyaltyProgram, Long> {

	/** Returns the one currently active program (endDate is null and active = true) */
	Optional<LoyaltyProgram> findByActiveTrueAndEndDateIsNull();

	/** Returns all programs ordered by most recent start date first */
	List<LoyaltyProgram> findAllByOrderByStartDateDesc();

	Optional<LoyaltyProgram> findByProgramCode(String programCode);
}
