package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;


    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            //1. 封装参数；可以使用微信提供的工具类将map转换为xml
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；在转换为xml的时候可以生成
            //paramMap.put("sign", null);
            //商品描述
            paramMap.put("body", "品优购-ee93");
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);
            //标价金额
            paramMap.put("total_fee", totalFee);
            //终端IP
            paramMap.put("spbill_create_ip", "127.0.0.1");
            //通知地址
            paramMap.put("notify_url", notifyurl);
            //交易类型
            paramMap.put("trade_type", "NATIVE");

            //生成签名了的xml内容
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付系统 统一下单 的数据为：" + signedXml);

            //2. 创建HttpClient对象发送请求；
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //3. 处理返回结果
            String content = httpClient.getContent();
            System.out.println("发送到微信支付系统 统一下单 的返回内容为：" + content);

            Map<String, String> map = WXPayUtil.xmlToMap(content);

            resultMap.put("outTradeNo", outTradeNo);
            resultMap.put("totalFee", totalFee);
            resultMap.put("result_code", map.get("result_code"));
            resultMap.put("code_url", map.get("code_url"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            //1. 封装参数；可以使用微信提供的工具类将map转换为xml
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；在转换为xml的时候可以生成
            //paramMap.put("sign", null);
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);

            //生成签名了的xml内容
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付系统 查询订单 的数据为：" + signedXml);

            //2. 创建HttpClient对象发送请求；
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //3. 处理返回结果
            String content = httpClient.getContent();
            System.out.println("发送到微信支付系统 查询订单 的返回内容为：" + content);

            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> closeOrder(String outTradeNo) {
        try {
            //1. 封装参数；可以使用微信提供的工具类将map转换为xml
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；在转换为xml的时候可以生成
            //paramMap.put("sign", null);
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);

            //生成签名了的xml内容
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("发送到微信支付系统 关闭订单 的数据为：" + signedXml);

            //2. 创建HttpClient对象发送请求；
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //3. 处理返回结果
            String content = httpClient.getContent();
            System.out.println("发送到微信支付系统 关闭订单 的返回内容为：" + content);

            return WXPayUtil.xmlToMap(content);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
