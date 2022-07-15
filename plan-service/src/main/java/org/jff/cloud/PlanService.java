package org.jff.cloud;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.TeachingDay;
import org.jff.cloud.entity.TeachingPlan;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.TeachingDayMapper;
import org.jff.cloud.mapper.TeachingPlanMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PlanService {

    private final TeachingPlanMapper teachingPlanMapper;

    private final TeachingDayMapper teachingDayMapper;


    public ResponseVO addPlan(TeachingPlan teachingPlan) {
        teachingPlanMapper.insert(teachingPlan);
        return new ResponseVO(ResultCode.SUCCESS, "添加教学计划成功");
    }
}
