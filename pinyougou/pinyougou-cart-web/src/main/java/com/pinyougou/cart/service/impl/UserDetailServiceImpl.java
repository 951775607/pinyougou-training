package com.pinyougou.cart.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    //用户在前端页面输入的用户名
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //构造用户的角色列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //在使用了cas之后，认证的工作交由cas进行；所以密码不需要指定而指定为空字符串即可。
        return new User(username, "", authorities);

    }

}
