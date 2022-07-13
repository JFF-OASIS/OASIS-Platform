package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    @TableId
    private Long studentId;

    private String name;

    private Long classId;

    private Long groupId;

    private Integer lineOfCode;

    private Integer score;

}
