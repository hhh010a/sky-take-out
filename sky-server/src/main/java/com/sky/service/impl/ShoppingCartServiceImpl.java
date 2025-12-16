package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    private final DishServiceImpl dishService;
    private final SetmealServiceImpl setmealService;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart= BeanUtil.toBean(shoppingCartDTO,ShoppingCart.class);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);

        if (shoppingCart.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }

        if (shoppingCart.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        if (shoppingCart.getDishFlavor() != null) {
            queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        }
        ShoppingCart cart=this.getOne(queryWrapper);

        if (cart != null) {
            cart.setNumber(cart.getNumber()+1);
            lambdaUpdate().eq(ShoppingCart::getUserId, userId).set(ShoppingCart::getNumber,cart.getNumber()).update();
        }else{
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                Dish dish=dishService.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal=setmealService.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            save(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> shoppingCartlist() {
        return lambdaQuery().eq(ShoppingCart::getUserId,BaseContext.getCurrentId()).list();
    }

    @Override
    public void clean() {
        lambdaUpdate().eq(ShoppingCart::getUserId,BaseContext.getCurrentId()).remove();
    }
}
