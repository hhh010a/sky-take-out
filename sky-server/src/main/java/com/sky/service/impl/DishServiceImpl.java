package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final DishFlavorServiceImpl dishFlavorService;
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish= BeanUtil.toBean(dishDTO,Dish.class);
        save( dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            for(DishFlavor flavor:flavors){
                flavor.setDishId(dishId);
            }
            dishFlavorService.saveBatch(flavors);
        }
    }
}
