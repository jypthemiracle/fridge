package com.poseidon.fridge.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import com.poseidon.food.model.Food;
import com.poseidon.fridge.model.Fridge;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FridgeRepositoryTests {
    
    @Autowired
    FridgeRepository repository;
    
    String nickname = "myFridge";
    Fridge fridge = Fridge.builder()
            .nickname(nickname)
            .build();
    
    @Test
    public void createFridge() {
        Fridge newFridge = repository.save(fridge);
        assertThat(newFridge).isNotNull();
        assertThat(newFridge.getNickname()).isEqualTo(nickname);
        assertThat(newFridge.getNickname()).isNotEqualTo("another nickname");
    }
    
    @Test
    public void update() {
        Fridge updatedFridge = repository.save(fridge);
        assertThat(repository.findById(updatedFridge.getId()).isPresent()).isTrue();
        
        Fridge replaceFridge = Fridge.builder()
                .id(updatedFridge.getId())
                .nickname("otherFridge")
                .build();
        repository.save(replaceFridge);
        
        Fridge savedFridge = repository.findById(replaceFridge.getId()).orElse(null);
        assertThat(savedFridge.getNickname()).isEqualTo(replaceFridge.getNickname());
    }
    
    @Test
    public void remove() {
        Fridge newFridge = repository.save(fridge);
        assertThat(repository.findById(newFridge.getId()).isPresent()).isTrue();
        assertThat(repository.count()).isEqualTo(1L);
        
        repository.deleteById(newFridge.getId());
        assertThat(repository.count()).isZero();
    }
    
    @Test
    public void createFridgeAndAddFoods() {
        Fridge newFridge = repository.save(fridge);
        assertThat(newFridge.getFoods()).isNullOrEmpty();
        
        Food milk = Food.builder()
                .name("파스퇴르 우유 1.8L")
                .quantity(1)
                .build();
        
        newFridge.addFood(milk);
        repository.flush();
        
        Fridge dbFridge = repository.findById(newFridge.getId()).orElse(null);
        assertThat(dbFridge.getFoods().size()).isEqualTo(1);
        assertThat(dbFridge.getFoods().get(0).getId()).isPositive();
        
        dbFridge.removeFood(milk);
        repository.flush();
        
        assertThat(dbFridge.getFoods().size()).isZero();
    }
    
    @Test
    public void findByUserId() {
        long firstUserId = 1004L;
        fridge.setUserId(firstUserId);
        
        repository.save(fridge);
        
        Fridge dbFirstFridge = repository.findByUserId(firstUserId)
                .orElse(new Fridge());
        assertThat(dbFirstFridge).isSameAs(fridge);
    }
    
    @Test(expected=IncorrectResultSizeDataAccessException.class)
    public void tooManyResult() {
        long userId = 1004L;
        Fridge fridge1 = Fridge.builder().nickname("fridge1").userId(userId).build();
        Fridge fridge2 = Fridge.builder().nickname("fridge2").userId(userId).build();
        repository.save(fridge1);
        repository.save(fridge2);
        
        repository.findByUserId(userId);
    }
    
    @Test
    public void whenCreateFridgeAndSaveThenAlreadyExistsCreatedDateAndLastModifiedDate() {
        Fridge fridge = Fridge.builder()
                .nickname("myFridge")
                .build();
        assertThat(fridge.getCreatedDate()).isNull();
        
        repository.save(fridge);
        assertThat(fridge.getCreatedDate()).isNotNull();
        assertThat(fridge.getCreatedDate()).isBetween(LocalDateTime.now().minusSeconds(1L), LocalDateTime.now());
        assertThat(fridge.getLastModifiedDate()).isNotNull();
        assertThat(fridge.getLastModifiedDate()).isEqualTo(fridge.getCreatedDate());
        
        fridge.setUserId(1004L);
        repository.flush();
        assertThat(fridge.getLastModifiedDate()).isNotEqualTo(fridge.getCreatedDate());
        assertThat(fridge.getLastModifiedDate()).isAfter(fridge.getCreatedDate());
    }
    
}
