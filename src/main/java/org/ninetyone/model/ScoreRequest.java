package org.ninetyone.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

public class ScoreRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String lastName;
    
    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score must be 0 or greater")
    @Max(value = 100, message = "Score must be 100 or less")
    private Integer score;
    
    public ScoreRequest() {
    }
    
    public ScoreRequest(String firstName, String lastName, Integer score) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.score = score;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
}