package org.jff.cloud;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jff.cloud.entity.File;
import org.springframework.stereotype.Component;

@Mapper
public interface MaterialMapper extends BaseMapper<File> {

    File findFileByKey(String key);
}
