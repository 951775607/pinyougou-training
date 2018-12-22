package com.pinyougou.manage.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/login")
@RestController
public class LoginController {

    /**
     * 获取当前登录用户的信息
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //从security中获取登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        resultMap.put("username", username);

        return resultMap;
    }
}
