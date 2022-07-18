package org.jff.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.dto.TeachingPlanDTO;
import org.jff.cloud.entity.TeachingDay;

import java.util.List;

@Mapper
public interface TeachingDayMapper extends BaseMapper<TeachingDay> {

}

