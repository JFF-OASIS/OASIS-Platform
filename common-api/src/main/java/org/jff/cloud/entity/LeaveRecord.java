package org.jff.cloud.entity;

import java.time.LocalDate;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRecord {

    @TableId(type= IdType.AUTO)
    private Long id;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String reason;
    
    private Long phoneNumber;
    
    private String department;
    
    private Long studentId;
    
    private Long engineerId;
    
    private Long teacherId;
    
    private Integer progress;
    
    private LeaveRecordStatus engineerStatus;
    
    private LeaveRecordStatus teacherStatus;
}

