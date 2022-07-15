package org.jff.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDTO {
    private Long classId;
    private String className;
    private Long teacherId;
    private Long engineerId;
    private Long teachingPlanId;
    private List<StudentDTO> students;
}
