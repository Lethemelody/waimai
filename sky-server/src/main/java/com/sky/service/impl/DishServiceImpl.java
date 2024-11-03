package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if(dishFlavorList != null && dishFlavorList.size() > 0){
            //向口味表插入数据
            dishFlavorList.forEach(dishFlavor ->{
               dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(dishFlavorList);
        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult( page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        //1，是否在售，2，有关联的套餐，3，删除后需要删除关联的口味数据
        if(dishMapper.queryAllDishIsSell(ids) > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        if (setmealMapper.getSetmealIdsByDishIds(ids).size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByIds(ids);
    }

    @Override
    public void UpdateStatus(Integer status,Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    @Override
    public DishVO getDishDetailWithFlavorById(Long id) {
        Dish dish = dishMapper.getDishDetailById(id);
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getFlavorByDishId(id);

        //封装到Vo中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }

    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        dishFlavorMapper.deleteById(dishDTO.getId());
        //删除原有的口味数据，在重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(dishDTO.getFlavors());
        }

    }


}
