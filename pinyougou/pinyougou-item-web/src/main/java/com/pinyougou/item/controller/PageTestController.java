package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/test")
@RestController
public class PageTestController {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;


    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    /**
     * 遍历每个spu id查询商品信息（6个）再利用Freemarker生成具体的该spu的静态页面到指定路径
     * @param goodsIds spu id数组
     * @return 操作结果
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                //生成静态页面
                genHtml(goodsId);
            }
        }
        return "success";
    }

    /**
     * 遍历每个spu id到指定路径下删除静态页面
     * @param goodsIds spu id数组
     * @return 操作结果
     */
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        try {
            if (goodsIds != null && goodsIds.length > 0) {
                for (Long goodsId : goodsIds) {
                    File file = new File(ITEM_HTML_PATH + goodsId + ".html");
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 根据spu id生成静态页面
     * @param goodsId spu id
     */
    private void genHtml(Long goodsId) {
        try {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();

            //模版
            Template template = configuration.getTemplate("item.ftl");

            //数据
            Map<String, Object> dataModel = new HashMap<>();

            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

            //itemList 商品sku列表
           dataModel.put("itemList", goods.getItemList());
            //goodsDesc 商品描述信息
           dataModel.put("goodsDesc", goods.getGoodsDesc());
            //goods 商品基本信息
           dataModel.put("goods", goods.getGoods());
            //itemCat1  第1级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
           dataModel.put("itemCat1", itemCat1.getName());
            //itemCat2  第2级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
           dataModel.put("itemCat2", itemCat2.getName());
            //itemCat3  第3级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
           dataModel.put("itemCat3", itemCat3.getName());

            //创建输出对象
            FileWriter fileWriter = new FileWriter(ITEM_HTML_PATH + goodsId + ".html");

            //输出
            template.process(dataModel, fileWriter);

            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
