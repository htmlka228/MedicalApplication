package org.example.repository;

import org.example.entity.Doctor;
import org.example.entity.Patient;
import org.example.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAll();
    List<Recipe> findByDoctor(Doctor doctor);
    List<Recipe> findByPriority(String priority);
    List<Recipe> findByPatient(Patient patient);
    List<Recipe> findByPatient_LastName(String name);
    List<Recipe> findByPatient_LastNameAndPriority(String name, String priority);
}
