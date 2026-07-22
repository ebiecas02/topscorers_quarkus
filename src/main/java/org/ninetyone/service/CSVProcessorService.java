package org.ninetyone.service;

import org.ninetyone.model.Person;
import org.ninetyone.model.PersonEntity;
import org.ninetyone.model.TopScorersResponse;
import org.ninetyone.repository.PersonRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class CSVProcessorService {
    
    private static final Logger LOG = Logger.getLogger(CSVProcessorService.class);
    
    @Inject
    PersonRepository personRepository;
    
    @Transactional
    public TopScorersResponse processCSV(InputStream inputStream) throws IOException {
        List<Person> people = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                LOG.info("Processing line " + lineNumber + ": " + line);
                
                String[] parts = line.split(",");
                
                // Skip header if it exists
                if (isFirstLine) {
                    String firstPart = parts[0].trim().toLowerCase();
                    boolean isHeader = firstPart.equals("name") || firstPart.equals("person") || 
                        firstPart.equals("student") || firstPart.equals("participant") ||
                        firstPart.equals("names") || firstPart.equals("players") ||
                        firstPart.equals("first name") || firstPart.equals("firstname") ||
                        firstPart.contains("name");
                    
                    if (isHeader) {
                        LOG.info("Skipping header row: " + line);
                        isFirstLine = false;
                        continue;
                    }
                    isFirstLine = false;
                }
                
                // Process data rows
                if (parts.length >= 2) {
                    try {
                        String firstName = "";
                        String lastName = "";
                        String fullName;
                        String scoreStr;
                        
                        // Handle different CSV formats
                        if (parts.length == 3) {
                            // Format: "First Name,Second Name,Score"
                            firstName = parts[0].trim();
                            lastName = parts[1].trim();
                            fullName = (firstName + " " + lastName).trim();
                            scoreStr = parts[2].trim();
                        } else {
                            // Format: "Name,Score"
                            fullName = parts[0].trim();
                            String[] nameParts = fullName.split(" ", 2);
                            if (nameParts.length == 2) {
                                firstName = nameParts[0];
                                lastName = nameParts[1];
                            } else {
                                firstName = fullName;
                                lastName = "";
                            }
                            scoreStr = parts[1].trim();
                        }
                        
                        // Handle cases where score might have extra spaces or quotes
                        scoreStr = scoreStr.replaceAll("\"", "").trim();
                        
                        int score = Integer.parseInt(scoreStr);
                        people.add(new Person(fullName, score));
                        
                        // Save to database
                        PersonEntity entity = new PersonEntity(firstName, lastName, score);
                        personRepository.persist(entity);
                        LOG.info("Saved to database: " + fullName + " - " + score);
                        
                    } catch (NumberFormatException e) {
                        LOG.warn("Skipping invalid row at line " + lineNumber + ": " + line + " - " + e.getMessage());
                    }
                } else {
                    LOG.warn("Skipping malformed row at line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException e) {
            LOG.error("Error reading CSV file", e);
            throw e;
        }
        
        LOG.info("Total people parsed and saved: " + people.size());
        
        if (people.isEmpty()) {
            LOG.warn("No valid data found in the CSV file.");
            return new TopScorersResponse(0, Collections.emptyList(), 0);
        }
        
        // Find the maximum score
        int topScore = 0;
        for (Person p : people) {
            if (p.getScore() > topScore) {
                topScore = p.getScore();
            }
        }
        
        LOG.info("Top score: " + topScore);
        
        // Get all people with the top score from the database
        List<PersonEntity> topScorersEntities = personRepository.findByScore(topScore);
        List<String> topScorers = new ArrayList<>();
        for (PersonEntity entity : topScorersEntities) {
            topScorers.add(entity.getFullName());
        }
        Collections.sort(topScorers);
        
        LOG.info("Top scorers: " + topScorers);
        
        return new TopScorersResponse(topScore, topScorers, people.size());
    }
    
@Transactional
public TopScorersResponse processCSVFromFile(String filePath) throws IOException {
    // Clear existing data before importing new data
    personRepository.clearAll();
    
    try (InputStream inputStream = new FileInputStream(filePath)) {
        return processCSV(inputStream);
    }
}
    
    public List<PersonEntity> getAllPersons() {
        return personRepository.listAll();
    }
    
    public List<PersonEntity> getTopScorersFromDatabase() {
        return personRepository.findTopScorersOrderedByName();
    }
}