package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {
    /**
     * 查询所有品牌数据
     * @return 品牌列表
     */
    @Deprecated
    List<TbBrand> queryAll();

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @Deprecated
    List<TbBrand> testPage(Integer page, Integer rows);


    /**
     * 根据品牌名称、首字母模糊分页查询品牌数据返回分页对象
     * @param page 页号
     * @param rows 页大小
     * @param brand 查询条件对象
     * @return 分页对象
     */
    PageResult search(Integer page, Integer rows, TbBrand brand);

    /**
     * 查询品牌列表
     * @return 品牌列表,数据结构如：[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
     */
    List<Map> selectOptionList();
}
