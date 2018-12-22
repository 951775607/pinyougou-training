package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService extends BaseService<TbSpecification> {

    PageResult search(Integer page, Integer rows, TbSpecification specification);

    /**
     * 新增规格及其规格选项列表
     * @param specification 规格及其规格选项列表
     */
    void add(Specification specification);

    /**
     * 根据规格id查询规格及其选项列表
     * @param id 规格id
     * @return 规格及其选项列表
     */
    Specification findOne(Long id);

    /**
     * 更新规格及其规格选项列表
     * @param specification 规格及其规格选项列表
     */
    void update(Specification specification);

    /**
     * 根据规格id集合删除规格及其每一个规格对应的所有的选项
     * @param ids 规格id集合
     */
    void deleteSpecificationByIds(Long[] ids);


    /**
     * 获取规格列表
     * @return 规格列表；数据结构如：[{"id":1,"text":"机身内存"},{"id":2,"text":"尺寸"}]
     */
    List<Map> selectOptionList();
}