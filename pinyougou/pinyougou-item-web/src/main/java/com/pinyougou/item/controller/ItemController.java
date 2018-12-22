package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    /**
     * 根据商品spu id查询商品基本、描述、sku列表（根据是否默认排序，降序排序），并加载商品1、2、3级商品分类中文名称。
     * @param goodsId 商品spu id
     * @return 视图名称和数据
     */
    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId){
        ModelAndView mv = new ModelAndView("item");

        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

        //itemList 商品sku列表
        mv.addObject("itemList", goods.getItemList());
        //goodsDesc 商品描述信息
        mv.addObject("goodsDesc", goods.getGoodsDesc());
        //goods 商品基本信息
        mv.addObject("goods", goods.getGoods());
        //itemCat1  第1级商品分类中文名称
        TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1", itemCat1.getName());
        //itemCat2  第2级商品分类中文名称
        TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2", itemCat2.getName());
        //itemCat3  第3级商品分类中文名称
        TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3", itemCat3.getName());

        return mv;
    }
}
