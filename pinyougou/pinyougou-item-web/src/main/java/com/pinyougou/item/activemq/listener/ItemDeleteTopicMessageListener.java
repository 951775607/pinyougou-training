package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 整合删除商品的主题，接收该主题的消息并根据消息删除指定路径下的静态html页面。
 */
public class ItemDeleteTopicMessageListener extends AbstractAdaptableMessageListener {

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
                File file = new File(ITEM_HTML_PATH + goodsId + ".html");
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }

}
