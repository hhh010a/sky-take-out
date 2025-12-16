package com.sky.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    private final SetmealDishServiceImpl setmealDishService;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;
    @Override
    public void saveWithSetmealDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal= BeanUtil.toBean(setmealDTO,Setmeal.class);
        save(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null&&setmealDishes.size()>0){
            for(SetmealDish setmealDish:setmealDishes){
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishService.saveBatch(setmealDishes);
        }
    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<SetmealVO> page = new Page<>(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        getBaseMapper().pageQuery(page,setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getRecords());
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        for(Long id:ids){
            Setmeal setmeal=getById(id);
            if(setmeal.getStatus()== StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        removeBatchByIds(ids);
        setmealDishService.lambdaUpdate().in(SetmealDish::getSetmealId,ids).remove();
    }

    @Override
    public SetmealVO getByIdWithDishes(Long id) {
        Setmeal setmeal=getById( id);
        SetmealVO setmealVO = BeanUtil.toBean(setmeal,SetmealVO.class);
        List<SetmealDish> setmealDishes = setmealDishService.lambdaQuery().eq(SetmealDish::getSetmealId,id).list();
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    public void updateWithDishes(SetmealDTO setmealDTO) {
        Setmeal setmeal=BeanUtil.toBean(setmealDTO,Setmeal.class);
        updateById(setmeal);
        setmealDishService.lambdaUpdate().eq(SetmealDish::getSetmealId,setmealDTO.getId()).remove();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes!=null&&setmealDishes.size()>0){
            for(SetmealDish setmealDish:setmealDishes){
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishService.saveBatch(setmealDishes);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        if(status== StatusConstant.ENABLE){
            List<Dish> dishes = dishMapper.dishList( id);
            if(dishes!=null&&dishes.size()>0){
                for(Dish dish:dishes){
                    if(dish.getStatus()== StatusConstant.DISABLE){
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }
        lambdaUpdate().eq(Setmeal::getId,id).set(Setmeal::getStatus,status).update();
    }

    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

}
