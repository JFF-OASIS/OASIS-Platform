package org.jff.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleClassDTO {

    private Long classId;

    private String className;

    private Long teacherId;

    private String teacherName;


}
