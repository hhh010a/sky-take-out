package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void saveWithSetmealDishes(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void deleteByIds(List<Long> ids);

    SetmealVO getByIdWithDishes(Long id);

    void updateWithDishes(SetmealDTO setmealDTO);

    void startOrStop(Integer status, Long id);
}
