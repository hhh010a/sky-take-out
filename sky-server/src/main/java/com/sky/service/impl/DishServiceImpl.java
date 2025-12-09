package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.SetmealDishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final DishFlavorServiceImpl dishFlavorService;
    private final SetmealDishMapper setmealDishMapper;
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

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        Page<DishVO> page = new Page(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        getBaseMapper().pageQuery(page,dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getRecords());
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        List<Dish> dishes = listByIds(ids);
        for (Dish dish : dishes) {
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }


        List<Long> setmealIds=setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds!=null&&setmealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        removeByIds(ids);

        dishFlavorService.lambdaUpdate().in(DishFlavor::getDishId,ids).remove();
    }


    public DishVO getByIdWithFlavor(@PathVariable Long id) {
        Dish dish= getById(id);
        DishVO dishVO = BeanUtil.toBean(dish,DishVO.class);
        List<DishFlavor> flavors = dishFlavorService.lambdaQuery().eq(DishFlavor::getDishId,id).list();
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish= BeanUtil.toBean(dishDTO,Dish.class);
        updateById(dish);
        dishFlavorService.lambdaUpdate().eq(DishFlavor::getDishId,dish.getId()).remove();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            for(DishFlavor flavor:flavors){
                flavor.setDishId(dish.getId());
            }
            dishFlavorService.saveBatch(flavors);
        }
    }
}
