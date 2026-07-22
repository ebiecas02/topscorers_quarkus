package org.ninetyone.model;

public class ScoreResponse {
    private String fullName;
    private int score;
    private String message;
    
    public ScoreResponse() {
    }
    
    public ScoreResponse(String fullName, int score) {
        this.fullName = fullName;
        this.score = score;
    }
    
    public ScoreResponse(String fullName, int score, String message) {
        this.fullName = fullName;
        this.score = score;
        this.message = message;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}