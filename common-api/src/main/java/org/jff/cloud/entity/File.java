package org.jff.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String type;

    private String url;

    private String key;

    private LocalDate uploadTime;

    private Long uploaderId;

}
