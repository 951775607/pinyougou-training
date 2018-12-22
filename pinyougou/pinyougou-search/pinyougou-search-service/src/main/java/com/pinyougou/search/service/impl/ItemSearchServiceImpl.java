package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //处理关键字中的空格
        if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
            searchMap.put("keywords", searchMap.get("keywords").toString().replaceAll(" ", ""));
        }

        //创建查询对象
        //SimpleQuery query = new SimpleQuery();
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //设置查询条件 is会对关键字分词
        Criteria criteria = new Criteria("item_title").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //设置高亮的配置信息
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮的域名
        highlightOptions.addField("item_title");
        //设置高亮的起始标签
        highlightOptions.setSimplePrefix("<font style='color:red'>");
        //设置高亮的结束标签
        highlightOptions.setSimplePostfix("</font>");
        query.setHighlightOptions(highlightOptions);

        //按照商品分类进行条件过滤
        if(!StringUtils.isEmpty(searchMap.get("category"))){
            //创建过滤查询对象
            SimpleFilterQuery categoryFilterQuery = new SimpleFilterQuery();
            //创建条件对象：参数1：域名，is之后的是查询的值
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            categoryFilterQuery.addCriteria(categoryCriteria);

            //添加过滤条件
            query.addFilterQuery(categoryFilterQuery);
        }
        //按照品牌进行条件过滤
        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            //创建过滤查询对象
            SimpleFilterQuery brandFilterQuery = new SimpleFilterQuery();
            //创建条件对象：参数1：域名，is之后的是查询的值
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            brandFilterQuery.addCriteria(brandCriteria);

            //添加过滤条件
            query.addFilterQuery(brandFilterQuery);
        }

        //按照规格进行条件过滤
        if(searchMap.get("spec") != null){

            //获取每一个规格 及其 值
            Map<String, String> specMap = (Map<String, String>) searchMap.get("spec");
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                //创建过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                //创建条件对象：参数1：域名，is之后的是查询的值
                Criteria specCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                filterQuery.addCriteria(specCriteria);

                //添加过滤条件
                query.addFilterQuery(filterQuery);
            }

        }

        //按照价格区间条件过滤
        if(!StringUtils.isEmpty(searchMap.get("price"))){

            //获取价格的上下限区间 500-1000， 3000-*
            String[] prices = searchMap.get("price").toString().split("-");

            //创建过滤查询对象
            SimpleFilterQuery priceStartFilterQuery = new SimpleFilterQuery();
            //创建条件对象：参数1：域名，is之后的是查询的值
            Criteria startCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            priceStartFilterQuery.addCriteria(startCriteria);
            //添加过滤条件
            query.addFilterQuery(priceStartFilterQuery);

            if (!"*".equals(prices[1])) {
                //处理如：3000-* 时候的*
                //创建过滤查询对象
                SimpleFilterQuery priceEndFilterQuery = new SimpleFilterQuery();
                //创建条件对象：参数1：域名，is之后的是查询的值
                Criteria endCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                priceEndFilterQuery.addCriteria(endCriteria);

                //添加过滤条件
                query.addFilterQuery(priceEndFilterQuery);
            }
        }

        //设置分页信息
        int pageNo = 1;
        if (searchMap.get("pageNo") != null) {
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        int pageSize = 20;
        if (searchMap.get("pageSize") != null) {
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        //起始索引号 = （页号-1）*页大小
        query.setOffset((pageNo-1)*pageSize);
        //页大小
        query.setRows(pageSize);

        //设置排序
        if (!StringUtils.isEmpty(searchMap.get("sortField")) && !StringUtils.isEmpty(searchMap.get("sort"))) {
            //域名
            String sortField = searchMap.get("sortField").toString();
            //顺序 DESC、ASC
            String sortStr = searchMap.get("sort").toString();

            //添加排序 参数1：排序顺序，参数2：在solr中的域名
            Sort sort = new Sort("DESC".equals(sortStr) ? Sort.Direction.DESC : Sort.Direction.ASC, "item_" + sortField);
            query.addSort(sort);
        }


        //查询
        //ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮标题
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {

                List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                //获取高亮的标题
                if(highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null){
                    //第一个0则表示高亮的域，取第一个；第二个get(0)表示域中的第一个高亮标题
                    String title = highlights.get(0).getSnipplets().get(0);
                    //设置回商品的标题
                    entry.getEntity().setTitle(title);
                }

            }
        }


        //返回
        resultMap.put("rows", highlightPage.getContent());
        //总记录数
        resultMap.put("total", highlightPage.getTotalElements());
        //总页数
        resultMap.put("totalPages", highlightPage.getTotalPages());

        return resultMap;
    }

    @Override
    public void importItemList(List<TbItem> itemList) {
        if (itemList != null && itemList.size() > 0) {
            for (TbItem tbItem : itemList) {
                Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
                tbItem.setSpecMap(specMap);
            }

            //更新
            solrTemplate.saveBeans(itemList);
            solrTemplate.commit();
        }
    }

    @Override
    public void deleteItemListByGoodsIds(List<Long> goodsIdsList) {
        SimpleQuery query = new SimpleQuery();

        Criteria criteria = new Criteria("item_goodsid").in(goodsIdsList);
        query.addCriteria(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
