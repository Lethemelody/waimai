package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);

    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    void deleteBatch(List<Long> ids);

    void UpdateStatus(Integer status,Long id);

    DishVO getDishDetailWithFlavorById(Long id);

    void updateDishWithFlavor(DishDTO dishDTO);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
