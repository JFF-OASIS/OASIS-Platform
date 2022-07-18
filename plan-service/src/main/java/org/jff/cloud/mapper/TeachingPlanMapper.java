package org.jff.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.dto.TeachingPlanDTO;
import org.jff.cloud.entity.TeachingPlan;

import java.util.List;

@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {


    List<TeachingPlanDTO> getTeachingPlan(Long id);
}
