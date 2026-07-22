package org.ninetyone.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ninetyone.model.PersonEntity;
import org.ninetyone.model.ScoreRequest;
import org.ninetyone.model.ScoreResponse;
import org.ninetyone.model.TopScorersResponse;
import org.ninetyone.repository.PersonRepository;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ScoreService {
    
    private static final Logger LOG = Logger.getLogger(ScoreService.class);
    
    @Inject
    PersonRepository personRepository;
    
    @Transactional
    public ScoreResponse addScore(ScoreRequest request) {
        LOG.info("Adding score for: " + request.getFirstName() + " " + request.getLastName());
        
        // Build full name
        String fullName;
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            fullName = (request.getFirstName().trim() + " " + request.getLastName().trim()).trim();
        } else {
            fullName = request.getFirstName().trim();
        }
        
        // Check if person already exists
        Optional<PersonEntity> existingPerson;
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            existingPerson = personRepository.findByFirstNameAndLastName(
                request.getFirstName().trim(), 
                request.getLastName().trim()
            );
        } else {
            existingPerson = personRepository.findByFullName(fullName);
        }
        
        if (existingPerson.isPresent()) {
            // Update existing person's score
            PersonEntity person = existingPerson.get();
            person.setScore(request.getScore());
            personRepository.persist(person);
            LOG.info("Updated score for: " + fullName + " to " + request.getScore());
            return new ScoreResponse(fullName, request.getScore(), "Score updated successfully");
        } else {
            // Create new person
            PersonEntity newPerson = new PersonEntity(
                request.getFirstName().trim(),
                request.getLastName() != null ? request.getLastName().trim() : "",
                request.getScore()
            );
            personRepository.persist(newPerson);
            LOG.info("Added new person: " + fullName + " with score " + request.getScore());
            return new ScoreResponse(fullName, request.getScore(), "Score added successfully");
        }
    }
    
    public ScoreResponse getScoreByFullName(String fullName) {
        LOG.info("Getting score for: " + fullName);
        
        Optional<PersonEntity> person = personRepository.findByFullName(fullName.trim());
        
        if (person.isPresent()) {
            PersonEntity entity = person.get();
            return new ScoreResponse(entity.getFullName(), entity.getScore());
        } else {
            return new ScoreResponse(fullName, 0, "Person not found");
        }
    }
    
    public ScoreResponse getScoreByFirstNameLastName(String firstName, String lastName) {
        LOG.info("Getting score for: " + firstName + " " + lastName);
        
        Optional<PersonEntity> person = personRepository.findByFirstNameAndLastName(
            firstName.trim(), 
            lastName.trim()
        );
        
        if (person.isPresent()) {
            PersonEntity entity = person.get();
            return new ScoreResponse(entity.getFullName(), entity.getScore());
        } else {
            String fullName = (firstName + " " + lastName).trim();
            return new ScoreResponse(fullName, 0, "Person not found");
        }
    }
    
    public TopScorersResponse getTopScorers() {
        LOG.info("Getting top scorers");
        
        List<PersonEntity> topScorersEntities = personRepository.findTopScorersOrderedByName();
        
        if (topScorersEntities.isEmpty()) {
            return new TopScorersResponse(0, List.of(), 0);
        }
        
        int topScore = topScorersEntities.get(0).getScore();
        List<String> names = topScorersEntities.stream()
                .map(PersonEntity::getFullName)
                .collect(Collectors.toList());
        
        long totalCount = personRepository.count();
        
        return new TopScorersResponse(topScore, names, (int) totalCount);
    }
}