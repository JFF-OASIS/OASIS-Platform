package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("class")
public class TeachingClass {

    @TableId
    private Long classId;

    private String className;

    private Long teachingPlanId;

    private Long engineerId;

    private Long teacherId;
}
