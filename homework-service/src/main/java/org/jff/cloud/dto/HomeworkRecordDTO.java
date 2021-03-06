package org.jff.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.HomeworkStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkRecordDTO {

    private Long homeworkRecordId;

    private Long studentId;

    private String studentName;

    private String contentUrl;

    private LocalDateTime submitTime;

    private HomeworkStatus submitStatus;

    private int score;

}
