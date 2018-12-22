package com.pinyougou.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    //在spring 4.0之后才有的泛型依赖注入
    @Autowired
    private Mapper<T> mapper;

    @Override
    public T findOne(Serializable id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() {
        return mapper.selectAll();
    }

    @Override
    public List<T> findByWhere(T t) {
        return mapper.select(t);
    }

    @Override
    public PageResult findPage(Integer page, Integer pageSize) {
        //设置分页
        PageHelper.startPage(page, pageSize);

        //查询
        List<T> list = mapper.selectAll();

        //创建分页信息对象
        PageInfo<T> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public PageResult findPage(Integer page, Integer pageSize, T t) {
        //设置分页
        PageHelper.startPage(page, pageSize);

        //查询
        List<T> list = mapper.select(t);

        //创建分页信息对象
        PageInfo<T> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void add(T t) {
        //选择性新增：如果对象中没有设置值的那些属性，则不会在操作语句中出现
        //如如果只给name：insert into tb_brand(name) values(?)
        //如如果只给name,firstChar：insert into tb_brand(name, first_char) values(?,?)
        mapper.insertSelective(t);
    }

    @Override
    public void update(T t) {
        //选择性更新：如果对象中没有设置值的那些属性，则不会在操作语句中出现
        //如如果只给id, name：update tb_brand set name =? where id=?
        //如如果只给id, name,firstChar：update tb_brand set name =?,first_char=? where id=?
        mapper.updateByPrimaryKeySelective(t);

    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        if(ids != null && ids.length > 0){
            for (Serializable id : ids) {
                mapper.deleteByPrimaryKey(id);
            }
        }
    }
}
