package org.jff.cloud.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.*;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.global.ResultCode;
import org.jff.cloud.mapper.RoleMapper;
import org.jff.cloud.mapper.StudentMapper;
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

import static org.jff.cloud.entity.RoleStatus.ROLE_STUDENT;
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

    private final StudentMapper studentMapper;

    private final UserRoleMapper userRoleMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public ResponseVO login(User user) {

        ScopedSpan authenticationSpan = tracer.startScopedSpan("authentication");
        //??????????????????
        log.info("login user: {}", user);

        //??????AuthenticationManager authenticate ????????????

        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword());

        ScopedSpan authenticateSpan = tracer.startScopedSpan("authenticate");
        Authentication authenticate = authenticationManager.authenticate(token);
        //?????????????????????????????????????????????
        if (!authenticate.isAuthenticated()) {
            throw new RuntimeException("????????????");
        }

        authenticateSpan.end();
        ScopedSpan jwtSpan = tracer.startScopedSpan("jwt");
        //???????????????????????????userid????????????jwt,jwt ??????ResponseResult????????????
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        String userid = loginUser.getUser().getUserId().toString();
        String jwt = JwtUtil.createJWT(userid);
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        jwtSpan.end();

        authenticationSpan.tag("Authentication","authentication");
        authenticationSpan.end();

        //??????????????????????????????redis, userid??????key
        redisCache.setCacheObject("login:"+userid, loginUser);

        return new ResponseVO(ResultCode.LOGIN_SUCCESS,map);

    }

    public ResponseVO logout() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        User loginUser = (User) authentication.getPrincipal();
        String userid = loginUser.getUserId().toString();

        //??????redis?????????
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
        //?????????user???
        userMapper.insert(user);
        //?????????role
        UserRoleRelation relation = new UserRoleRelation();
        relation.setUserId(user.getUserId());
        relation.setRoleId((long) userVO.getRole().ordinal()+1);
        userRoleMapper.insert(relation);
        if (userVO.getRole()==ROLE_STUDENT){
            Student student = Student.builder()
                    .studentId(userVO.getUserId())
                    .name(userVO.getUsername())
                    .classId(0L)
                    .groupId(0L)
                    .lineOfCode(0)
                    .score(0)
                    .build();

            //TODO:??????Rest??????
            studentMapper.insert(student);
        }
        return new ResponseVO(ResultCode.SUCCESS,"?????????????????????");

    }

    public String getUserRole(Long userId) {
        //????????????????????????????????????
        ScopedSpan querySpan = tracer.startScopedSpan("query");
        String role = roleMapper.findRolesByUserId(userId).get(0);
        querySpan.end();
        return role;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ????????????????????????
        Long userId = Long.parseLong(username);
        ScopedSpan queryUserSpan = tracer.startScopedSpan("queryUser");
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        User user = userMapper.selectOne(queryWrapper);


        //???????????????????????????
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
        relation.setRoleId((long) userVO.getRole().ordinal()+1);
        userRoleMapper.updateById(relation);

        return new ResponseVO(ResultCode.SUCCESS, "????????????????????????");
    }
}
