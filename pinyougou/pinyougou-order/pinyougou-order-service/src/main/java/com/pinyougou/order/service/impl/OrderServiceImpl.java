package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    //购物车在redis中key的名称
    private static final String CART_LIST = "CART_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String addOrder(TbOrder order) {
        String outTradeNo = "";
        //1. 获取redis中的购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(CART_LIST).get(order.getUserId());

        if (cartList != null && cartList.size() > 0) {
            //2. 遍历购物车列表，每个购物车对象Cart对应一个订单
            // 本次交易的总金额 = 所有订单的总金额
            double totalPayment = 0.0;
            //订单的id集合
            String orderIds = "";
            for (Cart cart : cartList) {
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(idWorker.nextId());

                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setPaymentType(order.getPaymentType());
                tbOrder.setUserId(order.getUserId());
                //未支付0
                tbOrder.setStatus("0");
                tbOrder.setSellerId(cart.getSellerId());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());

                //本笔订单支付总金额 = 所有订单明细的总金额之和
                double payment = 0.0;

                //3. 遍历购物车对象Cart中订单明细列表一个个的保存到订单明细表tb_order_item
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(tbOrder.getOrderId());

                    //累计本笔订单的总金额
                    payment += orderItem.getTotalFee().doubleValue();

                    //保存订单明细
                    orderItemMapper.insertSelective(orderItem);
                }

                //本笔订单支付总金额
                tbOrder.setPayment(new BigDecimal(payment));

                //累计本次交易的总金额
                totalPayment += payment;

                if (orderIds.length() > 0) {
                    orderIds += "," + tbOrder.getOrderId();
                } else {
                    orderIds = tbOrder.getOrderId().toString();
                }

                orderMapper.insertSelective(tbOrder);
            }

            //4. 如果为微信支付的话生成支付日志信息保存到tb_pay_log
            if("1".equals(order.getPaymentType())){
                TbPayLog payLog = new TbPayLog();
                outTradeNo = idWorker.nextId() + "";
                payLog.setOutTradeNo(outTradeNo);
                //1 未支付
                payLog.setTradeState("1");
                payLog.setPayType(order.getPaymentType());
                payLog.setUserId(order.getUserId());
                payLog.setCreateTime(new Date());
                //本次交易的总金额 = 所有订单的总金额；一般的电商对应价格都是使用整型的，不能使用小数点，因为小数点会出现精度不匹配的问题；
                //也就是金额的单位精确到分
                payLog.setTotalFee((long)(totalPayment * 100));

                //本次交易对应的所有订单id，使用,隔开
                payLog.setOrderList(orderIds);

                payLogMapper.insertSelective(payLog);
            }
            //5. 将redis中该用户对应的购物车数据删除
            redisTemplate.boundHashOps(CART_LIST).delete(order.getUserId());
        }
        //6. 返回支付日志id
        return outTradeNo;
    }

    @Override
    public TbPayLog findPayLogByOutTradeNo(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transaction_id) {
        //1、查询支付日志
        TbPayLog payLog = findPayLogByOutTradeNo(outTradeNo);
        //2、更新支付日志的支付状态为已支付
        payLog.setTradeState("1");
        payLog.setPayTime(new Date());
        payLogMapper.updateByPrimaryKeySelective(payLog);

        //3、更新本支付日志对应的所有订单的状态为已支付
        //获取所有订单的id数组
        String[] orderIds = payLog.getOrderList().split(",");
        //update tb_order set status = '2' where order_id  in(?,?)
        TbOrder order = new TbOrder();
        order.setStatus("2");

        Example example = new Example(TbOrder.class);
        example.createCriteria().andIn("orderId", Arrays.asList(orderIds));

        orderMapper.updateByExampleSelective(order, example);


    }
}
