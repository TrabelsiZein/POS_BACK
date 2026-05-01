package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.MemberFunction;
import com.digithink.pos.repository.MemberFunctionRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class MemberFunctionService extends _BaseService<MemberFunction, Long> {

	@Autowired
	private MemberFunctionRepository memberFunctionRepository;

	@Override
	protected _BaseRepository<MemberFunction, Long> getRepository() {
		return memberFunctionRepository;
	}
}
