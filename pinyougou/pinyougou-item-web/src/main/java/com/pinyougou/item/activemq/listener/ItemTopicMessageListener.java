package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 在详情系统配置消息监听器接收商品审核之后的主题，根据接收到的spu id数组利用Freemarker生成静态页面
 */
public class ItemTopicMessageListener extends AbstractAdaptableMessageListener {


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;


    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;


    @Override
    public void onMessage(Message message, Session session) throws JMSException {

        //1、接收消息、转换为数组
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //2、遍历每一个商品spu id 生成具体的静态页面
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                genHtml(goodsId);
            }
        }
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
