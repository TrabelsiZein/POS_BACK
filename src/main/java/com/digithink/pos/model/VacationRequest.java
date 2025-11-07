package com.digithink.vacation_app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.digithink.vacation_app.model.enumeration.VacationStatus;
import com.digithink.vacation_app.model.enumeration.VacationType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class VacationRequest extends _BaseEntity {

	@Column(nullable = false)
	private Long employeeId;
	private String employeeFullName;
	@Column(nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@Column(nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	private Long durationDays;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private VacationType vacationType;
	@Column(nullable = false)
	private String reason;
	@Column(columnDefinition = "TEXT")
	private String attachment;
	private String attachmentExtension;
	private String attachmentName;
	@Enumerated(EnumType.STRING)
	private VacationStatus vacationStatus = VacationStatus.Pending;
	private Long approverId;
	private String approverName;
	@JsonFormat(pattern = "yyyy-MM-dd | HH:mm:ss")
	private LocalDateTime approvalDate;
	private String rejectionReason;
	private Long delegatedToId;
	private String delegatedToFullName;
}
