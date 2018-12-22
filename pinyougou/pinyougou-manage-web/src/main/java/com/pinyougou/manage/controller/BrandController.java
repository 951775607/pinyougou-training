package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
//@Controller
@RestController //组合了@ResponseBody 和 @Controller ；对类中的所有方法生效
public class BrandController {

    //引入远程的服务对象
    @Reference
    private BrandService brandService;

    /**
     * 查询品牌列表
     * @return 品牌列表,数据结构如：[{"id":1,"text":"联想"},{"id":2,"text":"华为"}]
     */
    @GetMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

    /**
     * 根据品牌名称、首字母模糊分页查询品牌数据返回分页对象
     * @param page 页号
     * @param rows 页大小
     * @param brand 查询条件对象
     * @return 分页对象
     */
    @PostMapping("/search")
    public PageResult search(@RequestParam(defaultValue = "1")Integer page, @RequestParam(defaultValue = "10")Integer rows,
                             @RequestBody TbBrand brand){
        return brandService.search(page, rows, brand);
    }

    /**
     * 根据品牌id数组批量删除品牌数据
     * @param ids 品牌id数组批
     * @return 操作结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除品牌失败");
    }

    /**
     * 更加主键更新品牌数据到数据库中
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);

            return Result.ok("更新品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新品牌失败");
    }


    /**
     * 根据品牌主键查询品牌
     * @param id 品牌主键
     * @return 品牌
     */
    @GetMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 保存品牌数据到数据库中
     * @param brand 品牌
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);

            return Result.ok("新增品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增品牌失败");
    }

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 分页结果
     */
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows){
        return brandService.findPage(page, rows);
    }

    /**
     * 根据页号和页大小查询品牌列表
     * @param page 页号
     * @param rows 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer rows){
        //return brandService.testPage(page, rows);
        return (List<TbBrand>) brandService.findPage(page, rows).getRows();
    }

    /**
     * 查询所有品牌数据
     * @return 品牌列表json格式字符串
     */
    /*@RequestMapping(value="/findAll", method = RequestMethod.GET)
    @ResponseBody*/
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        //return brandService.queryAll();
        return brandService.findAll();
    }
}
