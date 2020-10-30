package org.example.service;

import org.example.entity.Patient;
import org.example.entity.Recipe;
import org.example.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    public void addRecipe(Recipe recipe){
        recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipes(){
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id){
        return recipeRepository.findById(id);
    }

    ///////////
    public List<Recipe> getRecipesByPatientLastName(String name){
        if(name.equals("")){
            return recipeRepository.findAll();
        }
        else{
            return recipeRepository.findByPatient_LastName(name);
        }
    }

    public List<Recipe> getRecipesByDescription(String description){
        List<Recipe> recipes = recipeRepository.findAll();
        List<Recipe> filterRecipes = new ArrayList<>();

        if(description.equals("")){
            return recipes;
        }

        for(Recipe el : recipes){
            if(el.getDescription().contains(description)){
                filterRecipes.add(el);
            }
        }

        return filterRecipes;
    }

    public List<Recipe> getRecipesByPriority(String priority){
        if(priority.equals("")){
            return recipeRepository.findAll();
        }
        return recipeRepository.findByPriority(priority);
    }

    public List<Recipe> getRecipesByLastNameAndPriority(String name, String priority){
        if(name.equals("") && priority.equals("")){
            return recipeRepository.findAll();
        }
        else if(name.equals("")){
            return getRecipesByPriority(priority);
        }
        else if(priority.equals("")){
            return getRecipesByPatientLastName(name);
        }
        else{
            return recipeRepository.findByPatient_LastNameAndPriority(name, priority);
        }
    }
    ///////////

    public void updateRecipe(Recipe recipe){
        recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipesByPatient(Patient patient){
        return recipeRepository.findByPatient(patient);
    }

    public void deleteRecipe(Recipe recipe){
        recipeRepository.delete(recipe);
    }
}
