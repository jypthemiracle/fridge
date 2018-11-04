package com.poseidon.food.command;

import java.time.LocalDate;
import java.time.Period;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poseidon.search.command.Category;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Food {
    
    private Long id;
    
    @NotNull
    @Size(min=1, max=40)
    private String name;
    
    @Min(1)
    @Max(999)
    private Integer quantity;
    
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate expiryDate;
    private Integer fridgeId;
    private Long categoryId;
    
    @JsonIgnore
    private Category category;
    
    @JsonIgnore
    public static final int SHOW_EXPIRY_D_DAYS = -3;
    
    @JsonIgnore
    public int getExpiryDays() {
        return Period.between(getExpiryDate(), LocalDate.now()).getDays();
    }
    
    @JsonIgnore
    public String showExpiryDDay() {
        if(getExpiryDays() >= SHOW_EXPIRY_D_DAYS) {
            if(getExpiryDays() == 0) {
                return "D-Day";
            } else if(getExpiryDays() < 0) {
                return "D" + getExpiryDays();
            } else if(getExpiryDays() > 0) {
                return "D+" + getExpiryDays();
            }
        }
        return null;
    }
    
    @Builder
    public Food(Long id, String name, Integer quantity, LocalDate expiryDate, Integer fridgeId) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.fridgeId = fridgeId;
    }
    
    @JsonIgnore
    public static final int DEFAULT_EXPIRY_DAYS = 7;
    
    @JsonIgnore
    public void setDefaultExpiryDate() {
        this.expiryDate = LocalDate.now().plusDays(DEFAULT_EXPIRY_DAYS);
    }

}
