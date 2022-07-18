package org.jff.cloud.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.dto.GroupDTO;
import org.jff.cloud.entity.Group;

import java.util.List;

@Mapper
public interface GroupMapper extends BaseMapper<Group> {
    List<GroupDTO> getGroupDTOList(Long classId);
    GroupDTO getGroupDTO(Long groupId);

}
