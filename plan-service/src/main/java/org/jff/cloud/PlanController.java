package org.jff.cloud;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.TeachingPlan;
import org.jff.cloud.global.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/plan")
public class PlanController {

    private final PlanService planService;

    @PostMapping()
    public ResponseVO addPlan(@RequestBody TeachingPlan teachingPlan) {
        log.info("addPlan: {}", teachingPlan);
        return planService.addPlan(teachingPlan);
    }

}
