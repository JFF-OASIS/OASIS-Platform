package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.TeachingPlanDTO;
import org.jff.cloud.entity.TeachingDay;
import org.jff.cloud.entity.TeachingPlan;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.TeachingDayMapper;
import org.jff.cloud.mapper.TeachingPlanMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PlanService {

    private final TeachingPlanMapper teachingPlanMapper;

    private final TeachingDayMapper teachingDayMapper;


    public ResponseVO addPlan(TeachingPlan teachingPlan) {
        log.info("addPlan: {}", teachingPlan);
        teachingPlanMapper.insert(teachingPlan);
        return new ResponseVO(ResultCode.SUCCESS, "添加教学计划成功");
    }

    public ResponseVO addDay(TeachingDay teachingDay) {
        log.info("addDay: {}", teachingDay);
        teachingDayMapper.insert(teachingDay);
        return new ResponseVO(ResultCode.SUCCESS, "添加教学天成功");
    }

    public ResponseVO deletePlan(Long teachingPlanId) {
        log.info("deletePlan: {}", teachingPlanId);
        teachingPlanMapper.deleteById(teachingPlanId);
        return new ResponseVO(ResultCode.SUCCESS, "删除教学计划成功");
    }

    public List<TeachingPlanDTO> getAllPlans() {

        List<TeachingPlanDTO> list = new ArrayList<>();

        List<TeachingPlan> teachingPlans = teachingPlanMapper.selectList(null);

        for (TeachingPlan teachingPlan : teachingPlans) {
            TeachingPlanDTO teachingPlanDTO = TeachingPlanDTO.builder()
                    .teachingPlanId(teachingPlan.getId())
                    .name(teachingPlan.getName())
                    .description(teachingPlan.getDescription())
                    .startDate(teachingPlan.getStartDate())
                    .endDate(teachingPlan.getEndDate())
                    .build();
            list.add(teachingPlanDTO);
        }

        return list;
    }

    public List<TeachingDay> getAllDays(Long teachingPlanId) {
        List<TeachingDay> teachingDays = teachingDayMapper.selectList(null);
        log.info("getAllDays: {}", teachingDays);
        return teachingDays;
    }

    public ResponseVO updateDay(TeachingDay teachingDay) {
        log.info("updateDay: {}", teachingDay);
        teachingDayMapper.updateById(teachingDay);
        return new ResponseVO(ResultCode.SUCCESS, "修改教学天成功");
    }

    public ResponseVO updatePlan(TeachingPlan teachingPlan) {
        log.info("updatePlan: {}", teachingPlan);
        teachingPlanMapper.updateById(teachingPlan);
        return new ResponseVO(ResultCode.SUCCESS, "修改教学计划成功");
    }
}
