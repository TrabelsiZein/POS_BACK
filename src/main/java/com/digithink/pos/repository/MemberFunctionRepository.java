package com.digithink.pos.repository;

import java.util.Optional;

import com.digithink.pos.model.MemberFunction;

public interface MemberFunctionRepository extends _BaseRepository<MemberFunction, Long> {

	Optional<MemberFunction> findByCode(String code);

}
