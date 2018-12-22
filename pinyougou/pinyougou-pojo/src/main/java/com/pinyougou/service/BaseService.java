package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体类
     */
    T findOne(Serializable id);

    /**
     * 查询全部
     * @return 列表
     */
    List<T> findAll();

    /**
     * 条件查询
     * @param t 条件对象
     * @return 列表
     */
    List<T> findByWhere(T t);

    /**
     * 分页查询
     * @param page 页号
     * @param pageSize 页大小
     * @return 分页对象（总记录数，列表）
     */
    PageResult findPage(Integer page, Integer pageSize);

    /**
     * 条件分页查询
     * @param page 页号
     * @param pageSize 页大小
     * @param t 条件
     * @return 分页对象（总记录数，列表）
     */
    PageResult findPage(Integer page, Integer pageSize, T t);

    /**
     * 新增
     * @param t 实体
     */
    void add(T t);

    /**
     * 根据主键更新
     * @param t 实体
     */
    void update(T t);

    /**
     * 批量删除
     * @param ids 要删除的那些主键数组
     */
    void deleteByIds(Serializable[] ids);
}
