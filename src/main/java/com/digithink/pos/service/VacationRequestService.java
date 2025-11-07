package com.digithink.vacation_app.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digithink.vacation_app.model.UserAccount;
import com.digithink.vacation_app.model.VacationRequest;
import com.digithink.vacation_app.model.enumeration.VacationStatus;
import com.digithink.vacation_app.repository.VacationRequestRepository;
import com.digithink.vacation_app.repository._BaseRepository;
import com.digithink.vacation_app.utils.Utils;

@Service
public class VacationRequestService extends _BaseService<VacationRequest, Long> {

	@Autowired
	private VacationRequestRepository vacationRequestRepository;
	@Autowired
	private Utils utils;

	public List<VacationRequest> findByCurrentUser() {
		return vacationRequestRepository.findByEmployeeIdOrderByIdDesc(utils.getCurrentUserAccount().getId());
	}

	@Override
	protected _BaseRepository<VacationRequest, Long> getRepository() {
		return this.vacationRequestRepository;
	}

	@Override
	public VacationRequest save(VacationRequest entity) throws Exception {
		// TODO Auto-generated method stub
		if (entity.getEndDate() != null && entity.getStartDate() != null) {
			long days = countWorkdays(entity.getStartDate(), entity.getEndDate());
			System.out.println(days);
			if (days < 0) {
				throw new Exception("Start Date must be before End Date");
			}
		}
		if (entity.getId() == null) {
			UserAccount currentUser = utils.getCurrentUserAccount();
			entity.setDurationDays(countWorkdays(entity.getStartDate(), entity.getEndDate()));
			entity.setEmployeeId(currentUser.getId());
			entity.setEmployeeFullName(currentUser.getFullName());
		}
		if (entity.getVacationStatus() == VacationStatus.Approved
				|| entity.getVacationStatus() == VacationStatus.Rejected) {
			UserAccount account = utils.getCurrentUserAccount();
			entity.setApproverId(account.getId());
			entity.setApproverName(account.getFullName());
			entity.setApprovalDate(LocalDateTime.now());
		}
		return super.save(entity);
	}

	public long countWorkdays(LocalDate start, LocalDate end) {
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Start date must be before end date");
		}

		return LongStream.rangeClosed(0, java.time.temporal.ChronoUnit.DAYS.between(start, end))
				.mapToObj(start::plusDays).filter(date -> isWorkday(date)).count();
	}

	private boolean isWorkday(LocalDate date) {
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
	}

}
