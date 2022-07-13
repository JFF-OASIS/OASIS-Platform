package org.jff.cloud.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {


    private User user;

    private List<String> roles;

    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;


    public LoginUser(User user, List<String> roles) {
        this.user = user;
        this.roles = roles;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把permissions中String类型的权限信息封装成SimpleGrantedAuthority对象
        if (authorities!=null){
            return authorities;
        }

        authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;

    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
