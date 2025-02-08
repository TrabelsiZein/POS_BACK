package com.digithink.vacation_app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.VacationRequest;
import com.digithink.vacation_app.repository.VacationRequestRepository;
import com.digithink.vacation_app.repository._BaseRepository;

@Service
public class VacationRequestService extends _BaseService<VacationRequest, Long> {

	@Autowired
	private VacationRequestRepository vacationRequestRepository;

	@Override
	protected _BaseRepository<VacationRequest, Long> getRepository() {
		return this.vacationRequestRepository;
	}

}
