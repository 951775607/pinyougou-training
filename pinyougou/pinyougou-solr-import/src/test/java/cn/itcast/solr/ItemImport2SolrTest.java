package cn.itcast.solr;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class ItemImport2SolrTest {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void test(){
        //1、查询已启用的商品sku列表
        TbItem param = new TbItem();
        //已启用
        param.setStatus("1");
        List<TbItem> itemList = itemMapper.select(param);

        //2、转换每一个sku中的spec到specMap
        for (TbItem tbItem : itemList) {
            Map map = JSONObject.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }

        //3、保存sku列表
        solrTemplate.saveBeans(itemList);

        //4、提交
        solrTemplate.commit();
    }
}
