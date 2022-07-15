package org.jff.cloud.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeachingDay {

    private Long id;

    private Long teachingPlanId;

    private String name;

    private String description;

    private LocalDate teachingDate;

}
