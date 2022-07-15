package org.jff.cloud.filter;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.LoginUser;
import org.jff.cloud.utils.JwtUtil;
import org.jff.cloud.utils.RedisCache;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;


/**
 * 1. 定义Jwt认证过滤器
 *    1. 获取token
 *    2. 解析token
 *    3. 获取其中的userid
 *    4. 从redis中获取用户信息
 *    5. 存入SecurityContextHolder中
 * */
@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    private final RedisCache redisCache;

    @Override
    //过滤器中doFilter方法前面的逻辑是请求进来时执行的内容，doFilter后面的逻辑是响应时执行的内容，直接return了，响应时就不会执行后面的内容了
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //如果没有token，直接放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userid;
        log.info("token:{}", token);
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
        String redisKey = "login:" + userid;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);//方法放行
        if(Objects.isNull(loginUser)){
            throw new RuntimeException("用户不存在");
        }
        //LoginUser implements UserDetails

        //存入SecurityContextHolder
        //TODO:获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        // principal 相当于用户名
        // credentials 相当于密码

        //直接设置已经认证
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}
