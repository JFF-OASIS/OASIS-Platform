package org.jff.cloud.entity;

import java.time.LocalDate;
import java.time.LocalTime;

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
public class AttendanceRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long studentId;
    
    private LocalDate date;
    
    private LocalTime exactTime;
    
    private AttendanceStatus status;
}

