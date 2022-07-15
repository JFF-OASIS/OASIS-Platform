package org.jff.cloud.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jff.cloud.entity.RoleStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    private Long userId;

    private String username;

    private String password;

    private RoleStatus role;
}
