package com.poseidon.food.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poseidon.food.command.Food;
import com.poseidon.fridge.service.FridgeClient;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/fridges/{fridgeId}/foods")
@SessionAttributes("food")
@RequiredArgsConstructor
public class FoodController {
    private final FridgeClient client;
    
    @GetMapping("/add")
    public String registerFoodForm(Food food, Model model) {
        model.addAttribute("food", food);
        return "foods/registerFoodForm";
    }
    
    @PostMapping("/add")
    public String processRegistrationFood(@ModelAttribute @Valid Food food, 
            Errors errors, 
            RedirectAttributes ra, 
            SessionStatus sessionStatus) {
        if(errors.hasErrors()) {
            return "foods/registerFoodForm";
        }
        
        if(food.getExpiryDate() == null) {
            food.setDefaultExpiryDate();
        }
        
        if(client.createFood(food) != null) {
            ra.addFlashAttribute("message", "식품을 저장했습니다.");
            sessionStatus.setComplete();
        }
        return "redirect:/fridges/me";
    }
    
    @GetMapping("/{id}")
    public String updateFoodForm(@PathVariable("fridgeId") Integer fridgeId, 
            @PathVariable long id, Model model) {
        Food food = client.loadFoodById(id);
        food.setFridgeId(fridgeId);
        model.addAttribute("food", food);
        return "foods/updateFoodForm";
    }
    
    @PutMapping("/{id}")
    public String processUpdateFood(@PathVariable long id, 
            @ModelAttribute @Valid Food food, 
            Errors errors, 
            RedirectAttributes ra,
            SessionStatus sessionStatus) {
        if(errors.hasErrors()) {
            return "foods/updateFoodForm";
        }
        
        client.updateFood(id, food);
        ra.addFlashAttribute("message", "식품을 저장했습니다.");
        sessionStatus.setComplete();
        return "redirect:/fridges/me";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteFood(@PathVariable long id, RedirectAttributes ra) {
        client.deleteFood(id);
        ra.addFlashAttribute("message", "식품을 삭제했습니다.");
        return "redirect:/fridges/me";
    }

}
