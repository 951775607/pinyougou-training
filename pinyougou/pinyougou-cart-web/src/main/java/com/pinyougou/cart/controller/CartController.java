package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {

    //品优购系统的购物车在cookie中的名称
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    //品优购系统的购物车在cookie中的生存时间为1天
    private static final int COOKIE_CART_MAX_AGE = 60*60*24;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    /**
     * 在登录、未登录情况下实现加入购物车
     * @param itemId 商品sku id
     * @param num 购买数量
     * @return 操作结果
     */
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num){

        //设置允许哪些域名的服务器可以获得资源，允许跨域请求响应
        //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
        //是否允许接收或设置cookie
        //response.setHeader("Access-Control-Allow-Credentials", "true");


        Result result = Result.fail("加入购物车失败");

        try {
            //1. 获取cookie中的购物车列表；
            List<Cart> cartList = findCartList();
            //2. 将新的商品和购买数量更新到购物车列表；
            List<Cart> newCartList = cartService.addItemToCartList(cartList, itemId, num);

            //因为配置了可以匿名访问所以如果是匿名访问的时候，返回的用户名为anonymousUser
            //如果未登录则用户名为：anonymousUser
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {
                //未登录；更新cookie中的数据

                //3. 将购物车列表写回到cookie；最大过期时间1天
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST,
                        JSON.toJSONString(newCartList), COOKIE_CART_MAX_AGE, true);
            } else {
                //已登录，更新redis中的数据
                //3、将购物车存入到redis中
                cartService.saveCartListByUsername(newCartList, username);
            }
            result = Result.ok("加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 登录或者未登录情况下购物车列表的查询
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        try {
            //因为配置了可以匿名访问所以如果是匿名访问的时候，返回的用户名为anonymousUser
            //如果未登录则用户名为：anonymousUser
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Cart> cookieCartList = new ArrayList<>();
            String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
            if (!StringUtils.isEmpty(cartListJsonStr)) {
                cookieCartList = JSONArray.parseArray(cartListJsonStr, Cart.class);
            }
            if ("anonymousUser".equals(username)) {
                //未登录；从cookie中获取购物车数据
                return cookieCartList;
            } else {
                //已登录，从redis中获取购物车数据
                List<Cart> redisCartList = cartService.findCartListByUsername(username);

                //1. 在登录之后；判断cookie中是否有购物车数据，有的话需要合并；
                if(cookieCartList.size() > 0) {
                    //2. 将cookie中的购物车列表与redis中的购物车列表合并到一个新的购物车列表；
                    redisCartList = cartService.mergeCartList(cookieCartList, redisCartList);
                    //3. 将新的购物车列表保存到redis中；
                    cartService.saveCartListByUsername(redisCartList, username);
                    //4. 删除cookie中的购物车；
                    CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
                }
                //5. 返回新的购物车列表；
                return redisCartList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前登录用户名
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<String, Object>();

        //因为配置了可以匿名访问所以如果是匿名访问的时候，返回的用户名为anonymousUser
        //如果未登录则用户名为：anonymousUser
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }
}
