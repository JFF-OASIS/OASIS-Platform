package org.jff.cloud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.ProjectStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectVO {

    private Long projectId;

    private String name;

    private String description;

    private Integer degreeOfDifficulty;

    private String technologyRequirement;

    private String language;
}
