package org.jff.cloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeworkRecord {

    private Long id;

    private Long studentId;

    private Long homeworkId;

    private LocalDateTime submitTime;

    private HomeworkStatus submitStatus;

    private int score;

    private String contentUrl;


}
