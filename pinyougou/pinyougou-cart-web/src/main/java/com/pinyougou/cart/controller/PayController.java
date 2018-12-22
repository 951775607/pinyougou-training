package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    /**
     * 根据交易号到支付系统查询订单的支付状态
     * @param outTradeNo 交易号
     * @return 操作结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败");
        try {
            //3分钟若未支付成功则认为超时；返回支付超时
            int count = 0;
            while(true) {
                //1. 定时每隔3秒到微信支付系统查询支付状态；
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                if(resultMap == null){
                    break;
                }
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    //2. 如果支付成功则更新订单信息；
                    orderService.updateOrderStatus(outTradeNo, resultMap.get("transaction_id"));
                    result = Result.ok("支付成功");
                    break;
                }

                count++;
                if (count > 60) {
                    result = Result.fail("支付超时");
                    break;
                }

                //每隔3秒
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3. 返回结果
        return result;
    }


    /**
     * 获取支付二维码链接、总金额、操作结果、交易号
     * @param outTradeNo 交易号（支付日志id）
     * @return 支付二维码链接、总金额、操作结果、交易号
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo){
        try {
            //1、根据支付日志查询支付日志信息获取总金额
            TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
            //本次要支付的总金额，精确到分
            String totalFee = payLog.getTotalFee().toString();

            //2、调用支付业务方法获取返回信息
            return weixinPayService.createNative(outTradeNo, totalFee);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

}
