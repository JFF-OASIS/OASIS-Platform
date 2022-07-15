package org.jff.cloud;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.TeachingPlanDTO;
import org.jff.cloud.entity.TeachingDay;
import org.jff.cloud.entity.TeachingPlan;
import org.jff.cloud.global.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/plan")
public class PlanController {

    private final PlanService planService;


    @GetMapping()
    //查看所有教学计划
    public List<TeachingPlanDTO> getAllPlans() {
        return planService.getAllPlans();
    }




    @PostMapping()
    //管理员新建教学计划
    public ResponseVO addPlan(@RequestBody TeachingPlan teachingPlan) {
        log.info("addPlan: {}", teachingPlan);
        return planService.addPlan(teachingPlan);
    }

    @PutMapping()
    //管理员修改教学计划
    public ResponseVO updatePlan(@RequestBody TeachingPlan teachingPlan) {
        log.info("updatePlan: {}", teachingPlan);
        return planService.updatePlan(teachingPlan);
    }

    @DeleteMapping()
    //管理员删除教学计划
    public ResponseVO deletePlan(@RequestParam Long teachingPlanId) {
        log.info("deletePlan: {}", teachingPlanId);
        return planService.deletePlan(teachingPlanId);
    }


    @GetMapping("/day")
    //查看所有教学天
    public List<TeachingDay> getAllDays(@RequestParam Long teachingPlanId) {
        return planService.getAllDays(teachingPlanId);
    }

    @PostMapping("/day")
    //工程师完善完善教学天
    public ResponseVO addDay(@RequestBody TeachingDay teachingDay) {

        log.info("addDay: {}", teachingDay);
        return planService.addDay(teachingDay);
    }

    @PutMapping("/day")
    //工程师修改教学天
    public ResponseVO updateDay(@RequestBody TeachingDay teachingDay) {
        log.info("updateDay: {}", teachingDay);
        return planService.updateDay(teachingDay);
    }





}
