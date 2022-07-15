package org.jff.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.entity.UserRoleRelation;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleRelation> {
}

