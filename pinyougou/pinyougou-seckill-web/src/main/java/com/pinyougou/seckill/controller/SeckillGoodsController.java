package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillGoods")
@RestController
public class SeckillGoodsController {

    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询可以显示的秒杀商品
     * @return 秒杀商品列表
     */
    @RequestMapping("/findList")
    public List<TbSeckillGoods> findList() {
        return seckillGoodsService.findList();
    }

    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillGoodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    /**
     * 根据id获取秒杀商品（redis）
     * @param id 秒杀商品id
     * @return 秒杀商品
     */
    @GetMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.findSeckillGoodsInRedisById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillGoodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seckillGoods 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeckillGoods seckillGoods, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillGoodsService.search(page, rows, seckillGoods);
    }

}
