package org.jff.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.LeaveRecord;
import org.jff.cloud.entity.LeaveRecordStatus;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRecordDTO {

    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    private Long phoneNumber;

    private String department;

    private Long studentId;

    private String studentName;

    private Long engineerId;

    private Long teacherId;

    private Integer progress;

    private LeaveRecordStatus engineerStatus;

    private LeaveRecordStatus teacherStatus;




}
