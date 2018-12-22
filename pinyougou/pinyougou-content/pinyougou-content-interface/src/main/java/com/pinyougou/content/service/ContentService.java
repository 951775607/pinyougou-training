package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface ContentService extends BaseService<TbContent> {

    PageResult search(Integer page, Integer rows, TbContent content);

    /**
     * 加载内容分类id为1并且状态为有效的那些可以使用的广告数据降序排序
     * @param categoryId 内容分类id
     * @return 内容列表
     */
    List<TbContent> findContentListByCategoryId(Long categoryId);
}