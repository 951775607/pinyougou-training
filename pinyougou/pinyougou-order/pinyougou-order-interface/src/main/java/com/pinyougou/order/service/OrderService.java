package com.pinyougou.order.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    /**
     * 根据购物车列表保存订单和明细和支付日志信息
     * @param order 订单信息
     * @return 支付日志id
     */
    String addOrder(TbOrder order);

    /**
     * 根据支付日志查询支付日志信息
     * @param outTradeNo 支付日志id
     * @return 支付日志
     */
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    /**
     * 根据本次交易的支付日志和对应的所有订单的支付状态为已支付
     * @param outTradeNo 支付日志id
     * @param transaction_id 微信支付订单号
     */
    void updateOrderStatus(String outTradeNo, String transaction_id);

}