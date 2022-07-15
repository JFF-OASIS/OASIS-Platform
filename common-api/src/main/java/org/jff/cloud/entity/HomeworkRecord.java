package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("submit_homework_record")
public class HomeworkRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long homeworkId;

    private LocalDateTime submitTime;

    private HomeworkStatus submitStatus;

    private int score;

    private String contentUrl;


}
