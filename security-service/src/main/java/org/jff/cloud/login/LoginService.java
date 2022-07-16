package org.jff.cloud.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.LoginUser;
import org.jff.cloud.entity.RoleStatus;
import org.jff.cloud.entity.User;
import org.jff.cloud.entity.UserRoleRelation;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.RoleMapper;
import org.jff.cloud.mapper.UserMapper;
import org.jff.cloud.mapper.UserRoleMapper;
import org.jff.cloud.utils.JwtUtil;
import org.jff.cloud.utils.RedisCache;
import org.jff.cloud.vo.UserVO;
import org.springframework.cloud.sleuth.ScopedSpan;
import org.springframework.cloud.sleuth.Tracer;
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

    private final Tracer tracer;

    private final RedisCache redisCache;

    private final UserMapper userMapper;

    private final RoleMapper roleMapper;

    private final UserRoleMapper userRoleMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public ResponseVO login(User user) {

        ScopedSpan authenticationSpan = tracer.startScopedSpan("authentication");
        //执行登录功能
        log.info("login user: {}", user);

        //获取AuthenticationManager authenticate 进行认证

        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword());

        ScopedSpan authenticateSpan = tracer.startScopedSpan("authenticate");
        Authentication authenticate = authenticationManager.authenticate(token);
        //如果认证没通过，给出对应的提示
        if (!authenticate.isAuthenticated()) {
            throw new RuntimeException("登录失败");
        }

        authenticateSpan.end();
        ScopedSpan jwtSpan = tracer.startScopedSpan("jwt");
        //如果认证通过，使用userid生成一个jwt,jwt 存入ResponseResult中并返回
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String userid = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createJWT(userid);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        jwtSpan.end();

        authenticationSpan.tag("Authentication","authentication");
        authenticationSpan.end();

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

    public ResponseVO register(UserVO userVO) {
        User user = new User();
        user.setUserId(userVO.getUserId());
        user.setUsername(userVO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userVO.getPassword()));
        user.setEmail(userVO.getEmail());
        //先插入user表
        userMapper.insert(user);
        //再插入role
        UserRoleRelation relation = new UserRoleRelation();
        relation.setUserId(user.getUserId());
        relation.setRoleId((long) userVO.getRole().ordinal());
        userRoleMapper.insert(relation);
        return new ResponseVO(ResultCode.SUCCESS);

    }

    public String getUserRole(Long userId) {
        //这里默认用户只有一个角色
        ScopedSpan querySpan = tracer.startScopedSpan("query");
        String role = roleMapper.findRolesByUserId(userId).get(0);
        querySpan.end();
        return role;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户基本信息
        ScopedSpan queryUserSpan = tracer.startScopedSpan("queryUser");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);


        //查询对应的权限信息
        List<String> roles = roleMapper.findRolesByUserId(user.getUserId());
        queryUserSpan.end();

        return new LoginUser(user, roles);
    }

    public ResponseVO updateUser(UserVO userVO) {
        User user = new User();
        user.setUserId(userVO.getUserId());
        user.setUsername(userVO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userVO.getPassword()));
        user.setEmail(userVO.getEmail());
        userMapper.updateById(user);


        QueryWrapper<UserRoleRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userVO.getUserId());
        UserRoleRelation relation = userRoleMapper.selectOne(queryWrapper);
        relation.setRoleId((long) userVO.getRole().ordinal());
        userRoleMapper.updateById(relation);

        return new ResponseVO(ResultCode.SUCCESS, "修改用户数据成功");
    }
}
