package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {

    PageResult search(Integer page, Integer rows, TbSeckillGoods seckillGoods);

    /**
     * 查询库存大于0，已审核，开始时间小于等于当前时间，结束时间大于当前时间的秒杀商品
     * @return 秒杀商品列表
     */
    List<TbSeckillGoods> findList();

    /**
     * 根据id获取秒杀商品（redis）
     * @param id 秒杀商品id
     * @return 秒杀商品
     */
    TbSeckillGoods findSeckillGoodsInRedisById(Long id);
}