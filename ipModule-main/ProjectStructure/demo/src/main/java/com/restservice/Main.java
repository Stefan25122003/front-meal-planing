package com.restservice;

//import com.restservice.recipeAndMealPlanning.recipe.RecommendationController;
import com.restservice.recipeAndMealPlanning.recipe.RecommendationSystem;
import com.restservice.shoppingListAndInventory.inventory.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "5000"));
        app.run(args);
    }
}