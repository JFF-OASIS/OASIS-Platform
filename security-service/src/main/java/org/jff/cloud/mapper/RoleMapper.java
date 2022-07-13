package org.jff.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.entity.Role;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<String> findRolesByUserId(Long userId);
}

