package org.example.service;

import org.example.entity.Doctor;
import org.example.entity.Recipe;
import org.example.repository.DoctorRepository;
import org.example.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id){
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsByName(String name){
        return doctorRepository.findByName(name);
    }

    public int getNumberRecipesByDoctor(Doctor doctor){
        List<Recipe> recipes = recipeRepository.findByDoctor(doctor);
        if(recipes == null || recipes.isEmpty()){
            return 0;
        }
        else{
            return recipes.size();
        }
    }

    public void addDoctor(Doctor doctor){
        doctorRepository.save(doctor);
    }
    public void updateDoctor(Doctor doctor){
        doctorRepository.save(doctor);
    }

    public void deleteDoctor(Doctor doctor){
        doctorRepository.delete(doctor);
    }
}
