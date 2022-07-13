package org.jff.cloud.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jff.cloud.entity.LoginUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SecurityUtil {

    public static Long getUserId() {
        LoginUser currentUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentUser.getUser().getUserId();
    }
}
