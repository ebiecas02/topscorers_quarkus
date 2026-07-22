package org.ninetyone.model;

import java.util.List;

public class TopScorersResponse {
    private int topScore;
    private List<String> topScorers;
    private int totalEntries;
    
    public TopScorersResponse() {
    }
    
    public TopScorersResponse(int topScore, List<String> topScorers, int totalEntries) {
        this.topScore = topScore;
        this.topScorers = topScorers;
        this.totalEntries = totalEntries;
    }
    
    public int getTopScore() {
        return topScore;
    }
    
    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }
    
    public List<String> getTopScorers() {
        return topScorers;
    }
    
    public void setTopScorers(List<String> topScorers) {
        this.topScorers = topScorers;
    }
    
    public int getTotalEntries() {
        return totalEntries;
    }
    
    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }
}