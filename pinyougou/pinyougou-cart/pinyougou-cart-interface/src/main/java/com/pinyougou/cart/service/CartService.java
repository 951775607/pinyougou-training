package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 将商品sku 和购买数量保存到购物车列表中
     * @param cartList 购物车列表
     * @param itemId 商品sku id
     * @param num 购买数量
     * @return 新的购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /***
     * 根据用户名查询在redis中的购物车列表
     * @param username 用户名
     * @return 购物车列表
     */
    List<Cart> findCartListByUsername(String username);

    /**
     * 将购物车数据存入到用户对应的域
     * @param newCartList 购物车列表
     * @param username 用户名
     */
    void saveCartListByUsername(List<Cart> newCartList, String username);

    /**
     * 合并两个购物车列表的数据
     * @param cookieCartList 购物车列表1
     * @param redisCartList 购物车列表2
     * @return 购物车列表
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);
}
