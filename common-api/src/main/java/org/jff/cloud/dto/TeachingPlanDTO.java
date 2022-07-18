package org.jff.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.TeachingDay;
import org.jff.cloud.entity.TeachingPlan;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeachingPlanDTO extends TeachingPlan {

    private List<TeachingDay> teachingDays;
}
