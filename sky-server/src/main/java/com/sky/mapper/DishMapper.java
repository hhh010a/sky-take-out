package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    Page<DishVO> pageQuery(@Param("page") Page<DishVO> page,@Param("dishPageQueryDTO") DishPageQueryDTO dishPageQueryDTO);

    @Select("select d.* from dish d left join setmeal_dish s on d.id=s.dish_id where s.setmeal_id =#{setmealId}")
    List<Dish> dishList(Long setmealId);

    List<Dish> list(Dish dish);
}
