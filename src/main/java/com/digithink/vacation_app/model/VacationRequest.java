package com.digithink.vacation_app.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.digithink.vacation_app.model.enumeration.VacationStatus;
import com.digithink.vacation_app.model.enumeration.VacationType;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class VacationRequest extends _BaseEntity {

	private Long employeeId;
	private String userFullName;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Long durationDays;
	private VacationType vacationType;
	private String reason;
	@Column(columnDefinition = "TEXT")
	private String attachment;
	private String attachmentExtension;
	private String attachmentName;
	private VacationStatus vacationStatus;
	private Long approverId;
	private String approverName;
	private LocalDateTime approvalDate;
	private String rejectionReason;
	private String cancellationReason;
	private Long delegatedToId;
	private String delegatedToFullName;
}
