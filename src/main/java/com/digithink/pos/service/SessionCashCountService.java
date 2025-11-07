package com.digithink.pos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.pos.model.SessionCashCount;
import com.digithink.pos.repository.SessionCashCountRepository;
import com.digithink.pos.repository._BaseRepository;

@Service
public class SessionCashCountService extends _BaseService<SessionCashCount, Long> {

	@Autowired
	private SessionCashCountRepository sessionCashCountRepository;

	@Override
	protected _BaseRepository<SessionCashCount, Long> getRepository() {
		return sessionCashCountRepository;
	}
}

