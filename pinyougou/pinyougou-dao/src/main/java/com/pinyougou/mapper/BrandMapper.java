package com.pinyougou.mapper;

import com.pinyougou.pojo.TbBrand;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface BrandMapper extends Mapper<TbBrand> {

    List<TbBrand> queryAll();

    /**
     * 查询品牌列表
     * @return 品牌列表,数据结构如：[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
     */
    List<Map> selectOptionList();
}
