package com.poseidon.food.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.poseidon.food.command.FoodCommand;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodRestService {
    private final RestTemplate fridgeServiceRestTemplate;
    
    public FoodCommand create(FoodCommand foodCommand) {
        ResponseEntity<FoodCommand> response = fridgeServiceRestTemplate.postForEntity("/foods", foodCommand, FoodCommand.class);
        if(response.getStatusCode() == HttpStatus.CREATED) {
            return response.getBody();
        }
        return null;
    }
    
    public FoodCommand loadById(long id) {
        try {
            ResponseEntity<FoodCommand> response = fridgeServiceRestTemplate.getForEntity("/foods/{id}", FoodCommand.class, id);
            if(response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }
        } catch(HttpClientErrorException ex) {
            log.error("Response error: {} {}", ex.getStatusCode(), ex.getStatusText());
        }
        return null;
    }
    
    public void update(FoodCommand foodCommand, long id) {
        fridgeServiceRestTemplate.put("/foods/{id}", foodCommand, id);
    }
    
    public void delete(long id) {
        fridgeServiceRestTemplate.delete("/foods/{id}", id);
    }

}
