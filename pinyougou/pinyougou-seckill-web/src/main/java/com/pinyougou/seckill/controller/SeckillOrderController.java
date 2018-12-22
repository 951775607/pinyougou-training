package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成秒杀订单并返回订单号
     * @param seckillId 秒杀商品id
     * @return 操作结果
     */
    @GetMapping("/submitOrder")
    public Result submitOrder(Long seckillId){
        Result result = Result.fail("秒杀失败!");
        try {
            //获取当前登录用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(username)) {
                String orderId = seckillOrderService.submitOrder(username, seckillId);
                if (orderId != null) {
                    result = Result.ok(orderId);
                }
            } else {
                result = Result.fail("请先登录之后再进行秒杀商品！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("/findAll")
    public List<TbSeckillOrder> findAll() {
        return seckillOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillOrderService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.add(seckillOrder);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillOrder seckillOrder) {
        try {
            seckillOrderService.update(seckillOrder);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            seckillOrderService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     * @param seckillOrder 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody  TbSeckillOrder seckillOrder, @RequestParam(value = "page", defaultValue = "1")Integer page,
                               @RequestParam(value = "rows", defaultValue = "10")Integer rows) {
        return seckillOrderService.search(page, rows, seckillOrder);
    }

}
