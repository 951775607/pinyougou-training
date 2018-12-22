package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSONArray;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;

public class MessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemSearchService searchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {

        //1、接收消息、转换为itemList列表
        TextMessage textMessage = (TextMessage) message;
        List<TbItem> itemList = JSONArray.parseArray(textMessage.getText(), TbItem.class);

        //2、更新到solr中
        searchService.importItemList(itemList);
    }
}
