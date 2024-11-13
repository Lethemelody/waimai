package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "菜品相关接口")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;



    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("查询参数：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("可批量删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids){
        log.info("删除的菜品id集合：{}",ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品销售状态")
    public Result modifyDishStatus(@PathVariable Integer status,Long id){
        log.info("修改的菜品为{}",id);
        dishService.UpdateStatus(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息用于修改菜品回显")
    public Result<DishVO> getDishDetailById(@PathVariable Long id){
        log.info("查询的菜品为{}",id);
        DishVO dishVo = dishService.getDishDetailWithFlavorById(id);
        return Result.success(dishVo);
    }

    @PutMapping
    @ApiOperation("修改菜品详情")
    public Result updateDishWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("修改菜品的参数为：{}",dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

}
