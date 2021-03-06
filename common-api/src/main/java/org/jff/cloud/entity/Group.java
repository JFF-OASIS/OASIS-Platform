package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("class_group")
public class Group {

    @TableId(type = IdType.AUTO)
    private Long groupId;

    private String groupName;

    private Long classId;

    private Long managerId;//组长Id

    private Long projectId;

    private Integer requirementAnalysisScore;

    private Integer designScore;

    private Integer projectDevelopmentScore;

    private Integer projectReportScore;

    private Integer qualityScore;

}
