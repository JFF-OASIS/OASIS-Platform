package org.jff.cloud.login;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jff.cloud.entity.User;
import org.jff.cloud.global.ResponseVO;
import org.jff.cloud.utils.SecurityUtil;
import org.jff.cloud.vo.UserVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class LoginController {

    private final LoginService loginService;

    private final SecurityUtil securityUtil;

    @PostMapping("/login")
    public ResponseVO login(@RequestBody User user) {
        return loginService.login(user);
    }

    @GetMapping("/logout")
    public ResponseVO logout() {
        return loginService.logout();
    }

    @PostMapping("/register")
    //注册用户，开发用
    public ResponseVO register(@RequestBody UserVO userVO) {
        return loginService.register(userVO);
    }


    @GetMapping()
    //查看个人资料
    public UserVO getUserInfo(@RequestParam Long userId){
        //TODO
        return null;
    }



    @PutMapping()
    //修改用户信息
    public ResponseVO updateUser(@RequestBody UserVO userVO){
        return loginService.updateUser(userVO);
    }

    @GetMapping("/role")
    public String getUserRole() {
        Long userId = securityUtil.getUserId();
        return loginService.getUserRole(userId);
    }

    @GetMapping("/testADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    public String testADMIN() {

        return "testADMIN";
    }

    @GetMapping("/testUSER")
    @PreAuthorize("hasRole('USER')")
    public String testUSER() {
        return "testUSER";
    }


}
