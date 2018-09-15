package com.poseidon.fridge.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.poseidon.food.model.Food;

public class FridgeTests {
    
    @Test
    public void newFridge() {
        String nickname = "myFridge";
        Fridge myFridge = new Fridge(nickname);
        assertThat(myFridge).isNotNull();
        assertThat(myFridge.getNickname()).isEqualTo(nickname);
        assertThat(myFridge.getNickname()).isNotEqualTo("Blah blah");
    }
    
    @Test
    public void newFridgeWithFoods() {
        String nickname = "myFridge";
        List<Food> foods = Arrays.asList(new Food("파스퇴르 우유 1.8L", 1, new Date()));
        Fridge fridge = new Fridge(nickname, foods);
        assertThat(fridge.getFoods()).containsOnlyElementsOf(foods);
        assertThat(fridge.hasFood()).isTrue();
        assertThat(fridge.getFoods().size()).isEqualTo(1);
        
        Food coke = new Food("코카콜라 500mL", 2, new Date());
        fridge.addFood(coke);
        assertThat(fridge.getFoods().size()).isGreaterThanOrEqualTo(2);
        assertThat(fridge.getFoods()).contains(coke);
        
        fridge.removeFood(coke);
        assertThat(fridge.getFoods().size()).isEqualTo(1);
        assertThat(fridge.getFoods()).containsOnlyElementsOf(foods);
    }

}
