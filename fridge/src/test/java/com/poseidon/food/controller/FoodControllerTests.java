package com.poseidon.food.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poseidon.food.model.Food;
import com.poseidon.food.repository.JpaFoodRepository;
import com.poseidon.food.service.FoodService;
import com.poseidon.fridge.model.Fridge;

@RunWith(SpringRunner.class)
@WebMvcTest(FoodController.class)
public class FoodControllerTests {
    
    @Autowired
    private MockMvc mvc;
    
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    
    @MockBean
    private JpaFoodRepository jpaFoodRepository;
    
    @MockBean
    private FoodService foodService;
    
    private Food milk = new Food.Builder("파스퇴르 우유 1.8L", 1)
            .id(ID)
            .fridge(new Fridge("myFridge"))
            .build();
    private static final Long ID = 1L;
    private static final String BASE_PATH = "http://localhost";
    
    @Test
    public void findAllFoods() throws Exception {
        List<Food> foods = Arrays.asList(milk);
        given(jpaFoodRepository.findAll()).willReturn(foods);
        
        final ResultActions result = mvc.perform(get("/foods").accept(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("_links.self.href", equalTo(BASE_PATH + "/foods")))
            .andExpect(jsonPath("_embedded.foodResourceList[0].id", equalTo(milk.getId().intValue())))
            .andExpect(jsonPath("_embedded.foodResourceList[0].name", equalTo(milk.getName())))
            .andExpect(jsonPath("_embedded.foodResourceList[0].quantity", equalTo(milk.getQuantity())))
            .andExpect(jsonPath("_embedded.foodResourceList[0].expiryDate", equalTo(milk.getExpiryDate().toString())))
            .andExpect(jsonPath("_embedded.foodResourceList[0].fridge.nickname", equalTo(milk.getFridge().getNickname())))
            .andExpect(jsonPath("_embedded.foodResourceList[0]._links.self.href", equalTo(BASE_PATH + "/foods/" + milk.getId().intValue())));
    }
    
    @Test
    public void findById() throws Exception {
        given(jpaFoodRepository.findOne(ID)).willReturn(milk);
        final ResultActions result = mvc.perform(get("/foods/" + ID));
        result.andExpect(status().isOk());
        verifyResultContent(result);
    }
    
    private void verifyResultContent(final ResultActions result) throws Exception {
        result
            .andExpect(jsonPath("id", equalTo(milk.getId().intValue())))
            .andExpect(jsonPath("name", equalTo(milk.getName())))
            .andExpect(jsonPath("quantity", equalTo(milk.getQuantity())))
            .andExpect(jsonPath("expiryDate", equalTo(milk.getExpiryDate().toString())))
            .andExpect(jsonPath("_links.self.href", equalTo(BASE_PATH + "/foods/" + milk.getId())))
            .andExpect(jsonPath("fridge.nickname", equalTo(milk.getFridge().getNickname())));
    }

    @Test
    public void postSave() throws Exception {
        given(foodService.save(any(Food.class))).willReturn(milk);
        final ResultActions result = mvc.perform(post("/foods")
                .content(mapper.writeValueAsString(milk))
                .contentType(MediaType.APPLICATION_JSON_UTF8));
        result.andExpect(status().isCreated())
            .andExpect(redirectedUrlPattern("**/foods/{id:\\d+}"));
        verifyResultContent(result);
    }
    
    @Test
    public void put() throws Exception {
        given(jpaFoodRepository.findOne(anyLong())).willReturn(milk);
        given(foodService.save(any(Food.class))).willReturn(milk);
        URI uri = UriComponentsBuilder.fromUriString("/foods/{id}").buildAndExpand(ID).toUri();
        mvc.perform(MockMvcRequestBuilders.put(uri)
                .content(mapper.writeValueAsString(milk))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }
    
    @Test
    public void delete() throws Exception {
        given(jpaFoodRepository.findOne(anyLong())).willReturn(milk);
        URI uri = UriComponentsBuilder.fromUriString("/foods/{id}").buildAndExpand(ID).toUri();
        mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }
    
    @Test
    public void deleteAll() throws Exception {
        doNothing().when(foodService).removeAll();
        URI uri = UriComponentsBuilder.fromUriString("/foods").build().toUri();
        mvc.perform(MockMvcRequestBuilders.delete(uri))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
        verify(foodService, times(1)).removeAll();
    }
    
}
