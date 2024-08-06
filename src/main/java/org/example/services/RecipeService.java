package org.example.services;

import org.example.models.Recipe;
import org.example.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository){
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes(){
        return this.recipeRepository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id){
        return this.recipeRepository.findById(id);
    }
}
