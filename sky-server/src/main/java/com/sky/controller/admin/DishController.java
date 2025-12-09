package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}",dishPageQueryDTO);
        return Result.success(dishService.page(dishPageQueryDTO));
    }

    @DeleteMapping
    public Result deleteByIds(@RequestParam List<Long> ids){
        log.info("批量删除：{}",ids);
        dishService.deleteByIds(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> getById(@PathVariable Long id){
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("编辑菜品：{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("员工状态：{}",status);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        log.info("查询菜品：{}",categoryId);
        return Result.success(dishService.list(categoryId));

    }
}
