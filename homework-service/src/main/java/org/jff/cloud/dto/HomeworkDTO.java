package org.jff.cloud.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.HomeworkStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkDTO {

    private Long homeworkId;

    private String questionContent;

    private LocalDate publishTime;

    private LocalDateTime deadline;

}
