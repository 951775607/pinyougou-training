package com.pinyougou.shop.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {

    //@Reference
    private SellerService sellerService;

    //用户在前端页面输入的用户名
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据商家id查询商家
        TbSeller seller = sellerService.findOne(username);
        //商家存在并且已经审核通过才进行认证
        if (seller != null && "1".equals(seller.getStatus())) {
            //构造用户的角色列表
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

            //将使用前端输入的密码与给定的密码进行匹配，如果一致则登录认证成功
            return new User(username, seller.getPassword(), authorities);
        }

        return null;
    }

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }
}
