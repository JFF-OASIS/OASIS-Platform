package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @TableId(type = IdType.AUTO)
    private Long projectId;

    private String name;

    private String description;

    private ProjectStatus status;

    private Integer degreeOfDifficulty;

    private String technologyRequirement;

    private String language;

    private Integer countOfFile;

    private Integer lineOfCode;

    private Integer qualityOfCode;


}
