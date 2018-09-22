package com.poseidon.food.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.poseidon.food.model.Food;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FoodRepositoryTests {
    
    @Autowired
    FoodRepository repository;
    
    private Food cola = Food.builder()
            .name("코카콜라 500mL")
            .quantity(2)
            .expiryDate(LocalDate.of(2018, 9, 10))
            .build();
    
    @Test
    public void save() {
        repository.save(cola);
        Food food = repository.findById(cola.getId())
                .orElse(null);
        assertThat(food.getName()).isEqualTo(cola.getName());
        assertThat(food.getExpiryDate()).isEqualTo("2018-09-10");
    }
    
    @Test
    public void remove() {
        repository.save(cola);
        assertThat(repository.findAll().size()).isEqualTo(1);
        
        repository.deleteById(cola.getId());
        assertThat(repository.findAll().size()).isEqualTo(0);
    }
    
    @Test
    public void whenCreateFoodAndSaveThenAlreadyExistsCreatedDateAndLastModifiedDate() {
        assertThat(cola.getCreatedDate()).isNull();
        repository.save(cola);
        
        assertThat(cola.getCreatedDate()).isNotNull();
        assertThat(cola.getCreatedDate()).isBetween(LocalDateTime.now().minusSeconds(1L), LocalDateTime.now());
        assertThat(cola.getLastModifiedDate()).isNotNull();
        assertThat(cola.getLastModifiedDate()).isEqualTo(cola.getCreatedDate());
        
        cola.setQuantity(1);
        repository.flush();
        assertThat(cola.getLastModifiedDate()).isNotEqualTo(cola.getCreatedDate());
        assertThat(cola.getLastModifiedDate()).isAfter(cola.getCreatedDate());
    }
    
}
