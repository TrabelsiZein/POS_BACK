package com.digithink.vacation_app.repository;

import java.util.List;

import com.digithink.vacation_app.model.VacationRequest;

public interface VacationRequestRepository extends _BaseRepository<VacationRequest, Long> {

	List<VacationRequest> findByEmployeeIdOrderByIdDesc(Long employeeId);

}
