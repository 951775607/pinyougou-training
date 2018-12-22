package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {

    PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder);

    /**
     * 生成秒杀订单并返回订单号
     * @param seckillId 秒杀商品id
     * @return 操作结果
     */
    String submitOrder(String username, Long seckillId) throws Exception;

    /**
     * 根据秒杀订单查询秒杀订单
     * @param outTradeNo 秒杀订单id
     * @return 秒杀订单
     */
    TbSeckillOrder findSeckillOrderByOutTradeNo(String outTradeNo);

    /**
     * 将redis中的订单更新保存到mysql并删除redis中的订单
     * @param outTradeNo 秒杀订单id
     * @param transaction_id 微信支付系统的订单号
     */
    void saveOrderInRedisToDb(String outTradeNo, String transaction_id);

    /**
     * 订单被关闭则需要将redis中的订单删除并加回库存
     * @param outTradeNo 订单id
     */
    void deleteSeckillOrderInRedis(String outTradeNo) throws InterruptedException;
}