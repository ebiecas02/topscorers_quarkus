package org.ninetyone.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ninetyone.model.PersonEntity;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository implements PanacheRepository<PersonEntity> {
    
    public List<PersonEntity> findByScore(int score) {
        return list("score", score);
    }
    
    public List<PersonEntity> findTopScorers() {
        Integer maxScore = find("select max(score) from PersonEntity").project(Integer.class).firstResult();
        if (maxScore == null || maxScore == 0) {
            return List.of();
        }
        return findByScore(maxScore);
    }
    
    public List<PersonEntity> findTopScorersOrderedByName() {
        Integer maxScore = find("select max(score) from PersonEntity").project(Integer.class).firstResult();
        if (maxScore == null || maxScore == 0) {
            return List.of();
        }
        return list("score = ?1 order by fullName", maxScore);
    }
    
    public Optional<PersonEntity> findByFullName(String fullName) {
        return find("fullName = ?1", fullName).firstResultOptional();
    }
    
    public Optional<PersonEntity> findByFirstNameAndLastName(String firstName, String lastName) {
        return find("firstName = ?1 and lastName = ?2", firstName, lastName).firstResultOptional();
    }
    
    public void clearAll() {
        deleteAll();
    }
}