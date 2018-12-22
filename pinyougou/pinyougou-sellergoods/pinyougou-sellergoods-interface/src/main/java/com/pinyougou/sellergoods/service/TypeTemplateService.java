package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {

    PageResult search(Integer page, Integer rows, TbTypeTemplate typeTemplate);

    /**
     * 获取分类模版id并查询所期望的数据;结构如：
     * [
     *     {"id":27,"text":"网络","options":[{"id":123,"optionName":"2G","orders":"1"},{"id":123,"optionName":"3G","orders":"3"}]},
     *
     *  {"id":32,"text":"机身内存","options":[{"id":123,"optionName":"32G","orders":"1"},{"id":123,"optionName":"64G","orders":"2"}]}
     * ]
     * @param id 分类模版id
     * @return 规格及其规格选项
     */
    List<Map> findSpecList(Long id);
}