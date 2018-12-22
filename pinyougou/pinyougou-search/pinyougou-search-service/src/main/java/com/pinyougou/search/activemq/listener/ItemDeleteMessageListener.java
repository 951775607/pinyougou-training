package com.pinyougou.search.activemq.listener;


import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService searchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {

        //1、接收消息、转换为数组
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //2、更新到solr中
        searchService.deleteItemListByGoodsIds(Arrays.asList(goodsIds));
    }
}
