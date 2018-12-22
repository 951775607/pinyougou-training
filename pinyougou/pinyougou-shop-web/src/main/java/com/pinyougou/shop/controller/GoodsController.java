package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 商品的基本、描述、sku列表信息之后要保存到数据库中
     * @param goods 商品信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //获取当前登录的用户（商家）设置商家信息
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            //未审核
            goods.getGoods().setAuditStatus("0");
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    /**
     * 根据spu id查询商品基本、描述、sku列表
     * @param id spu id
     * @return 商品基本、描述、sku列表
     */
    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    /**
     * 根据商品spu id保存商品基本、描述、sku列表；
     * @param goods 商品基本、描述、sku列表
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //当前登录商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

            //获取商品原来的商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());

            if(sellerId.equals(goods.getGoods().getSellerId()) && sellerId.equals(oldGoods.getSellerId())) {
                goodsService.updateGoods(goods);
            } else {
                return Result.fail("非法操作");
            }
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param goods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbGoods goods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        //当前的登录用户就是商家；应该只查询该商家的商品
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page, rows, goods);
    }

    /**
     * 根据商品spu id数组更新那些商品的状态
     * @param ids 商品spu id数组
     * @param status 商品的状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids, status);

            return Result.ok("更新商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新商品状态失败");
    }

}
