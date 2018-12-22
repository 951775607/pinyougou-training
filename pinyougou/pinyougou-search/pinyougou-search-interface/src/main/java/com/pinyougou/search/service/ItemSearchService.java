package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索条件搜索商品
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 批量更新solr中的商品数据
     * @param itemList sku列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 删除solr中的商品
     * @param goodsIdsList spu id数组--》goods_id
     */
    void deleteItemListByGoodsIds(List<Long> goodsIdsList);
}
