package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    /**
     * 商品的基本、描述、sku列表信息之后要保存到数据库中
     * @param goods 商品信息
     */
    void addGoods(Goods goods);

    /**
     * 根据spu id查询商品基本、描述、sku列表
     * @param id spu id
     * @return 商品基本、描述、sku列表
     */
    Goods findGoodsById(Long id);

    /**
     * 根据商品spu id保存商品基本、描述、sku列表；
     * @param goods 商品基本、描述、sku列表
     */
    void updateGoods(Goods goods);

    /**
     * 根据商品spu id数组更新那些商品的状态
     * @param ids 商品spu id数组
     * @param status 商品的状态
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 更新商品的删除状态为已删除
     * @param ids 要删除的商品spu id数组
     */
    void deleteGoodsByIds(Long[] ids);

    /**
     * 根据商品spu id数组查询这些spu对应的已启用的sku列表
     * @param ids 商品spu id数组
     * @param status sku的状态
     * @return sku列表
     */
    List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status);

    /**
     * 根据商品spu id查询商品基本、描述、sku列表（根据是否默认排序，降序排序），并加载商品1、2、3级商品分类中文名称。
     * @param goodsId 商品spu id
     * @param itemStatus 商品sku 状态
     * @return 商品信息
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus);
}