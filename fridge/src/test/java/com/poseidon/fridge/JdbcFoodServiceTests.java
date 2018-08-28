package com.poseidon.fridge;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.poseidon.fridge.model.Food;
import com.poseidon.fridge.repository.JdbcFoodRepository;
import com.poseidon.fridge.service.JdbcFoodService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class JdbcFoodServiceTests {
    
    @Autowired
    private JdbcFoodService jdbcFoodService;
    
    @Autowired
    private JdbcFoodRepository jdbcFoodRepository;
    
    @Before
    public void setUp() {
        jdbcFoodService.removeAll();
    }
    
    @Test
    public void save() {
        Food cola = new Food("코카콜라 500mL", 2, "2018-10-30");
        cola = jdbcFoodService.save(cola);
        assertThat(cola.getId(), notNullValue());
        
        cola.decreaseQuantity(1);
        jdbcFoodService.save(cola);
        
        Food savedCola = jdbcFoodRepository.findById(cola.getId());
        assertThat(savedCola.getQuantity(), equalTo(1));
    }
    
    @Test
    public void remove() {
        Food milk = new Food("파스퇴르 우유 1.8L", 1, "2018-09-28");
        jdbcFoodService.save(milk);
        Long id = milk.getId();
        assertThat(jdbcFoodRepository.findById(id), notNullValue());
        
        boolean result = jdbcFoodService.remove(milk);
        assertThat(result, equalTo(true));
    }
    
    @Test
    public void removeById() {
        Long lastId = 0L;
        List<Food> foods = Arrays.asList(new Food("코카콜라 500mL", 2, "2018-10-30"), 
                new Food("파스퇴르 우유 1.8L", 1, "2018-09-28"));
        for(Food food : foods) {
            jdbcFoodService.save(food);
            lastId = food.getId();
        }
        jdbcFoodService.remove(lastId);
        assertThat(jdbcFoodRepository.findAll().size(), equalTo(1));
    }
    
}
