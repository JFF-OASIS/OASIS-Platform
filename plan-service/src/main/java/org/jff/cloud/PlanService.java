package org.jff.cloud;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.dto.SimpleTeachingPlanDTO;
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

    public List<SimpleTeachingPlanDTO> getAllPlans() {

        List<SimpleTeachingPlanDTO> list = new ArrayList<>();

        List<TeachingPlan> teachingPlans = teachingPlanMapper.selectList(null);

        for (TeachingPlan teachingPlan : teachingPlans) {
            SimpleTeachingPlanDTO teachingPlanDTO = SimpleTeachingPlanDTO.builder()
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

    public List<TeachingPlanDTO> getTeachingPlan(Long teachingPlanId) {
        return teachingPlanMapper.getTeachingPlan(teachingPlanId);
    }

    public Long getTeachingPlanId(Long teachingDayId) {
        QueryWrapper<TeachingDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", teachingDayId);
        TeachingDay teachingDay = teachingDayMapper.selectOne(queryWrapper);
        return teachingDay.getTeachingPlanId();
    }
}
