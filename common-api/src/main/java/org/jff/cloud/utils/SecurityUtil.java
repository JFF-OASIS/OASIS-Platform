package org.jff.cloud.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jff.cloud.entity.LoginUser;
import org.jff.cloud.entity.Role;
import org.jff.cloud.entity.RoleStatus;
import org.springframework.data.redis.domain.geo.RadiusShape;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SecurityUtil {

    public Long getUserId() {
        LoginUser currentUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUser().getUserId();
    }


    public RoleStatus getUserRole(){
        //注意这里默认一个用户只有一个角色
        LoginUser currentUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return RoleStatus.valueOf(currentUser.getRoles().get(0));
    }

}
