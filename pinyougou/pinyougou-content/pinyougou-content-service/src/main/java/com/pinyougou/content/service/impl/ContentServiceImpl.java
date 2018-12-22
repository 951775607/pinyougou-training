package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //广告内容缓存在redis中的key的名称
    private static final String CONTENT = "content";

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void add(TbContent tbContent) {
        super.add(tbContent);
        //同步更新内容分类对应的缓存数据
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    /**
     * 同步更新内容分类对应的缓存数据
     * @param categoryId 内容分类id
     */
    private void updateContentListInRedisByCategoryId(Long categoryId) {
        redisTemplate.boundHashOps(CONTENT).delete(categoryId);
    }

    @Override
    public void update(TbContent tbContent) {
        //1、查询旧内容
        TbContent oldContent = findOne(tbContent.getId());

        super.update(tbContent);

        //2、更新缓存数据
        if (!tbContent.getCategoryId().equals(oldContent.getCategoryId())) {
            //查询内容对应的原内容分类；如果内容分类与当前最新的内容分类不一致，则需要删除旧分类对应的缓存
            updateContentListInRedisByCategoryId(oldContent.getCategoryId());
        }

        //将内容对应的新内容分类的缓存数据从redis中删除
        updateContentListInRedisByCategoryId(tbContent.getCategoryId());
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        //先根据内容id数组查询所有内容；遍历每一个内容再根据内容分类到redis删除分类对应的缓存数据
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> contentList = contentMapper.selectByExample(example);
        if (contentList != null && contentList.size() > 0) {
            for (TbContent content : contentList) {
                updateContentListInRedisByCategoryId(content.getCategoryId());
            }
        }

        super.deleteByIds(ids);
    }

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;

        try {
            //1. 先从redis查询内容列表，如果找到则直接返回；
            contentList = (List<TbContent>) redisTemplate.boundHashOps(CONTENT).get(categoryId);
            if (contentList != null) {
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * --查询内容分类id为1并且有效的广告并且按照排序字段降序排序
         * select * from tb_content where category_id=? and status=1 order by sort_order desc
         */
        Example example = new Example(TbContent.class);

        Example.Criteria criteria = example.createCriteria();

        //有效
        criteria.andEqualTo("status", "1");

        //分类
        criteria.andEqualTo("categoryId", categoryId);

        //排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //2. 如果在redis中不存在内容列表，则从mysql根据条件查询；返回数据之前将数据存入到redis
            redisTemplate.boundHashOps(CONTENT).put(categoryId, contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentList;
    }
}
