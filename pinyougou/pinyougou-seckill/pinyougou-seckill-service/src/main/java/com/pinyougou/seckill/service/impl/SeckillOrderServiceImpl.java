package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.RedisLock;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service(interfaceClass = SeckillOrderService.class)
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    //秒杀订单列表在redis的key的名称
    private static final String SECKILL_ORDERS = "SECKILL_ORDERS";
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;
    //秒杀商品列表在redis的key的名称
    private static final String SECKILL_GOODS = "SECKILL_GOODS";

    @Override
    public PageResult search(Integer page, Integer rows, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seckillOrder.get***())){
            criteria.andLike("***", "%" + seckillOrder.get***() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String submitOrder(String username, Long seckillId) throws Exception {
        //   加分布式锁，锁定要秒杀的商品
        RedisLock redisLock = new RedisLock(redisTemplate);
        if(redisLock.lock(seckillId.toString())) {
            //1. 获取秒杀商品；判断秒杀商品是否存在和库存大于0；
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillId);
            if (seckillGoods == null) {
                throw new RuntimeException("秒杀商品不存在");
            }
            if (seckillGoods.getStockCount() == 0) {
                throw new RuntimeException("商品已经秒杀完。");
            }
            //2. 将商品的库存减1
            seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
            if(seckillGoods.getStockCount() == 0) {
                //   2.1、库存为0的话那么需要将该秒杀商品更新回到mysql，并从redis中删除
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);

                redisTemplate.boundHashOps(SECKILL_GOODS).delete(seckillId);
            } else {
                //   2.2、库存不为0的话那么直接更新redis中的秒杀商品
                redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillId, seckillGoods);
            }
            //释放锁
            redisLock.unlock(seckillId.toString());
            //3. 创建一个秒杀订单；
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            seckillOrder.setId(idWorker.nextId());
            seckillOrder.setSeckillId(seckillId);
            //未支付
            seckillOrder.setStatus("0");
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            //支付总金额 秒杀价格
            seckillOrder.setMoney(seckillGoods.getCostPrice());

            //4. 存入秒杀订单到redis中；
            redisTemplate.boundHashOps(SECKILL_ORDERS).put(seckillOrder.getId().toString(), seckillOrder);

            return seckillOrder.getId().toString();
        }
        //5. 返回订单号
        return null;
    }

    @Override
    public TbSeckillOrder findSeckillOrderByOutTradeNo(String outTradeNo) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(outTradeNo);
    }

    @Override
    public void saveOrderInRedisToDb(String outTradeNo, String transaction_id) {
        //1、获取在redis中的订单；
        TbSeckillOrder seckillOrder = findSeckillOrderByOutTradeNo(outTradeNo);
        seckillOrder.setStatus("1");
        seckillOrder.setPayTime(new Date());
        seckillOrder.setTransactionId(transaction_id);

        //2、保存订单到mysql;
        seckillOrderMapper.insertSelective(seckillOrder);

        //3、删除redis中的订单
        redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
    }

    @Override
    public void deleteSeckillOrderInRedis(String outTradeNo) throws InterruptedException {
        //获取订单
        TbSeckillOrder seckillOrder = findSeckillOrderByOutTradeNo(outTradeNo);
        //加分布式锁
        RedisLock redisLock = new RedisLock(redisTemplate);
        if(redisLock.lock(seckillOrder.getSeckillId().toString())) {
            //1、获取秒杀商品
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(seckillOrder.getSeckillId());
            //2、更新商品库存
            //如果秒杀商品不存在则从mysql中查询
            if (seckillGoods == null) {
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
            }
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            //更新回redis
            redisTemplate.boundHashOps(SECKILL_GOODS).put(seckillGoods.getId(), seckillGoods);

            //释放分布式锁
            redisLock.unlock(seckillOrder.getSeckillId().toString());
            //3、删除redis中订单
            redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
        }
    }
}
