package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

//暴露服务，也就是将该服务注册到注册中心;并在ioc中存在该对象
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        //设置分页；只针对紧接着执行的sql语句生效
        PageHelper.startPage(page, rows);

        return brandMapper.selectAll();
    }

    @Override
    public PageResult search(Integer page, Integer rows, TbBrand brand) {

        //设置分页信息
        PageHelper.startPage(page, rows);

        //模糊条件查询 select * from tb_brand where name like "%条件%" and first_char = "条件"
        Example example = new Example(TbBrand.class);

        //创建查询条件对象；相当于where子句
        Example.Criteria criteria = example.createCriteria();

        //首字母查询
        //if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar())) {
        if(!StringUtils.isEmpty(brand.getFirstChar())){
            criteria.andEqualTo("firstChar", brand.getFirstChar());
        }

        //名字模糊查询
        if(!StringUtils.isEmpty(brand.getName())) {
            criteria.andLike("name", "%" + brand.getName() + "%");
        }

        //如果有需要排序
        //example.orderBy("name");


        //返回分页对象
        List<TbBrand> list = brandMapper.selectByExample(example);

        //创建分页信息对象
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);

        //返回
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
