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
public class AuditProjectVO {

    private Long projectId;

    private ProjectStatus status;

}
