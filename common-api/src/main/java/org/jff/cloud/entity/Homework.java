package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Homework {
    @TableId(type = IdType.AUTO)
    private Long homeworkId;

    private Long classId;

    private String name;

    private LocalDateTime publishTime;

    private LocalDateTime deadline;

    private String content;

}
