package org.jff.cloud;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.entity.Project;
import org.jff.cloud.vo.ProjectVO;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {



}
