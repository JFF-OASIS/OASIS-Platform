package org.jff.cloud.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.LoginUser;
import org.jff.cloud.entity.User;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.RoleMapper;
import org.jff.cloud.mapper.UserMapper;
import org.jff.cloud.utils.JwtUtil;
import org.jff.cloud.utils.RedisCache;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jff.cloud.global.ResultCode.LOGOUT_SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService implements UserDetailsService {

    private final AuthenticationManager authenticationManager;


    private final RedisCache redisCache;

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public ResponseVO login(User user) {
        //执行登录功能

        //获取AuthenticationManager authenticate 进行认证

        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword());


        Authentication authenticate = authenticationManager.authenticate(token);
        //如果认证没通过，给出对应的提示
        if (!authenticate.isAuthenticated()) {
            throw new RuntimeException("登录失败");
        }


        //如果认证通过，使用userid生成一个jwt,jwt 存入ResponseResult中并返回
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String userid = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createJWT(userid);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);

        //把完整的用户信息存入redis, userid作为key
        redisCache.setCacheObject("login:"+userid, loginUser);

        return new ResponseVO(ResultCode.LOGIN_SUCCESS,map);

    }

    public ResponseVO logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        User loginUser = (User) authentication.getPrincipal();
        String userid = loginUser.getUserId().toString();

        //删除redis中的值
        redisCache.deleteObject("login:" + userid);
        log.info("logout userid:{}", userid);
        return new ResponseVO(LOGOUT_SUCCESS, null);
    }

    public ResponseVO register(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        return new ResponseVO(ResultCode.SUCCESS);

    }

    public String getUserRole(Long userId) {
        //这里默认用户只有一个角色
        return roleMapper.findRolesByUserId(userId).get(0);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户基本信息
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);


        //查询对应的权限信息
        List<String> roles = roleMapper.findRolesByUserId(user.getUserId());


        return new LoginUser(user, roles);
    }
}
