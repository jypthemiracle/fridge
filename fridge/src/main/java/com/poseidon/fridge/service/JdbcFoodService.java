package com.poseidon.fridge.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.poseidon.fridge.model.Food;
import com.poseidon.fridge.repository.JdbcFoodRepository;

@Service
public class JdbcFoodService implements FoodService {
    
    @Autowired
    private JdbcFoodRepository jdbcFoodRepository;
    
    @Override
    public Food save(Food food) {
        if(food.getId() == null) {
            jdbcFoodRepository.insert(food);
        } else {
            jdbcFoodRepository.update(food);
        }
        return food;
    }

    @Override
    public List<Food> findAll() {
        return jdbcFoodRepository.findAll();
    }

    @Override
    public boolean remove(Food food) {
        return jdbcFoodRepository.remove(food) == 1;
    }

    @Override
    public Food findById(Long id) {
        return jdbcFoodRepository.findById(id);
    }

}
