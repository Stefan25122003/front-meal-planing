//package com.restservice.recipeAndMealPlanning.recipe;
//
//import jakarta.persistence.criteria.CriteriaBuilder;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.Reader;
//import java.time.Duration;
//import java.util.*;
//
//import org.apache.commons.lang3.StringUtils;
//
//@RestController
//@RequestMapping("/recipes")
//public class RecipeController {
//
//    private static final Recipe defaultRecipe = new Recipe(
//            1,
//            "Pasta",
//            1,
//            "John Doe",
//            Duration.ofMinutes(30),
//            Duration.ofMinutes( 10),
//            Duration.ofMinutes( 40),
//            "2021-09-01",
//            "A simple pasta recipe",
//            new ArrayList<String>(),
//            "Main Course",
//            new ArrayList<String>(),
//            new ArrayList<Float>(),
//            new ArrayList<String>(),
//            new ArrayList<String>(),
//            0.0f,
//            1000.0f,
//            10.0f,
//            5.0f,
//            0.0f,
//            0.0f,
//            100.0f,
//            10.0f,
//            0.0f,
//            0.0f,
//            4f,
//            "4 servings",
//            new HashMap<>()
//
//    );
//
//    @Autowired
//    private RecipeRepository recipeRepository;
//    @Autowired
//    private RecipeService recipeService;
//
//    @GetMapping("/default")
//    public Recipe getDefaultRecipe() {
//        recipeRepository.save(defaultRecipe);//saving in the db test! works!
//        return defaultRecipe;
//    }
//
//    //so i dont accidentally set up again; will b uncommented when ill b working with db population again
//    @GetMapping("/setupDB")
//    public String setupDB() {
//        try {
//            recipeService.importDB();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "DB setup failed!";
//        }
//
//        return "DB setup done!";
//    }
//
//    @GetMapping("/getRecommendations/{userId}")
//    public Map<String, Recipe> getRecommendations(@PathVariable int userId) {
//        RecommendationSystem recommendationSystem = new RecommendationSystem(recipeService);
//        List<Integer> recommendations = recommendationSystem.getAllRecommendations().get(userId);
//
//        // Create a map to store the recipe names and corresponding recipes
//        Map<String, Recipe> recipeRecommendations = new LinkedHashMap<>();
//
//        // Iterate over the list of recommended recipe IDs
//        for (int i = 0; i < recommendations.size(); i++) {
//            int recipeId = recommendations.get(i);
//            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
//            if (recipe != null) {
//                // Add the recipe to the map with a key in the format "Recipe X"
//                recipeRecommendations.put("Recipe " + (i + 1), recipe);
//            }
//        }
//
//        return recipeRecommendations;
//    }
//
//    @GetMapping("/RecipeAI")
//    public Recipe generateRecipe() {
//        RecipeAIServer recipeAIServer = new RecipeAIServer();
//        Recipe generatedRecipe = recipeAIServer.generateRecipe();
//        return generatedRecipe;
//    }
//
//    /*@GetMapping("/removeBadRecipes")
//    public String removeBadRecipes() {
//        try {
//            recipeService.removeBadRecipes();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Bad recipes removed failed!";
//        }
//
//        return "Bad recipes removed done!";
//    }*/
//
//
//    /*@GetMapping("/removeColumns")
//    public String removeColumns() {
//        try {
//            recipeService.deleteUnwantedColumns();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Columns removed failed!";
//        }
//
//        return "Columns removed done!";
//    }*/
//
//}


package com.restservice.recipeAndMealPlanning.recipe;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.*;

import org.apache.commons.lang3.StringUtils;


@RestController
@RequestMapping("/api/v1/recipes/")
public class RecipeController {

    private static final Recipe defaultRecipe = new Recipe(
            1,
            "Pasta",
            1,
            "John Doe",
            Duration.ofMinutes(30),
            Duration.ofMinutes( 10),
            Duration.ofMinutes( 40),
            "2021-09-01",
            "A simple pasta recipe",
            new ArrayList<String>(),
            "Main Course",
            new ArrayList<String>(),
            new ArrayList<Float>(),
            new ArrayList<String>(),
            new ArrayList<String>(),
            0.0f,
            1000.0f,
            10.0f,
            5.0f,
            0.0f,
            0.0f,
            100.0f,
            10.0f,
            0.0f,
            0.0f,
            4f,
            "4 servings",
            new HashMap<>()

    );

    @Autowired
    protected RecipeRepository recipeRepository;
    @Autowired
    protected RecipeService recipeService;

    @Autowired
    protected RecipeUserPreferencesRepository recipeUserPreferencesRepository;

    @Autowired
    protected RecipeUserPreferencesService recipeUserPreferencesService;


    // Tested with Postman (http://localhost:5000/api/v1/recipes/defaultRecipe)
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping("/defaultRecipe")
    public Recipe getDefaultRecipe() {
        recipeRepository.save(defaultRecipe); //saving in the db test! works!
        return defaultRecipe;
    }


    // Tested with Postman (http://localhost:5000/api/v1/recipes/recipe/38)
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping(value = "/recipe/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe getRecipe(@PathVariable int id) {
        return recipeRepository.findById(id).orElse(null);
    }


    //Tested with Postman (http://localhost:5000/api/v1/recipes/getRecommendations/1)
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping(value="/getRecommendations/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Recipe> getRecommendations(@PathVariable int userId) {
        RecommendationSystem recommendationSystem = new RecommendationSystem(recipeService, recipeRepository);
        List<Integer> recommendations = recommendationSystem.getAllRecommendations().get(userId);

        // Create a map to store the recipe names and corresponding recipes
        Map<String, Recipe> recipeRecommendations = new LinkedHashMap<>();

        // Iterate over the list of recommended recipe ids
        for (int i = 0; i < recommendations.size(); i++) {
            int recipeId = recommendations.get(i);
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe != null) {
                // Add the recipe to the map with a key in the format "Recipe X"
                recipeRecommendations.put("Recipe " + (i + 1), recipe);
            }
        }

        return recipeRecommendations;
        //return recommendationSystem.categorizeRecommendationsForUser(recipeRecommendations);
        // group recipeRecommendations by keywords
    }

    // Postman: http://localhost:5000/api/v1/recipes/getRecommendations/1/categorized)
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping(value="/getRecommendations/{userId}/categorized", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<Integer>> getUserCategorizedRecommendations(@PathVariable("userId") int userId) {
        RecommendationSystem recommendationSystem = new RecommendationSystem(recipeService, recipeRepository);
        List<Integer> recommendations = recommendationSystem.getAllRecommendations().get(userId);
        return recommendationSystem.categorizeRecommendationsForUser(recommendations);
    }

    // Tested with Postman (http://localhost:5000/api/v1/recipes/RecipeAI?recipeFormat=youtube&input=apple pie recipe&mainIngredients=apple)
    // Error in backend: CreateProcess error=2, The system cannot find the file specified
    //@CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @PostMapping(value="/RecipeAI", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe generateRecipe(@RequestParam String recipeFormat, @RequestParam String input, @RequestParam String mainIngredients) {
        RecipeAIServer recipeAIServer = new RecipeAIServer();
        Recipe generatedRecipe = recipeAIServer.generateRecipe(recipeFormat, input, mainIngredients);
        return generatedRecipe;
    }

    // Tested with Postman (http://localhost:5000/api/v1/recipes/addRecipe)
    // Body example: {
    //  "recipeId": 1,
    //  "recipeTitle": "Pasta",
    //  "authorId": 1,
    //  "authorName": "John Doe",
    //  "cookTime": "PT30M",
    //  "prepTime": "PT10M",
    //  "totalTime": "PT40M",
    //  "datePublished": "2021-09-01",
    //  "description": "A simple pasta recipe",
    //  "imageList": [],
    //  "category": "Main Course",
    //  "keywords": [],
    //  "ingredientsMap": {
    //
    //  },
    //  "printableIngredients": [],
    //  "reviewCount": 0,
    //  "calories": 1000,
    //  "fatContent": 10,
    //  "saturatedFatContent": 5,
    //  "cholesterolContent": 0,
    //  "sodiumContent": 0,
    //  "carbohydrateContent": 100,
    //  "fiberContent": 10,
    //  "sugarContent": 0,
    //  "proteinContent": 0,
    //  "recipeServings": 4,
    //  "recipeYield": "4 servings",
    //  "instructionsList": {
    //
    //  },
    //  "ingredients": []
    //}
    @PostMapping(value="/addRecipe", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe addRecipe(@RequestBody Recipe newRecipe) {
        System.out.println(newRecipe.toString());
        return recipeRepository.save(newRecipe);
    }


    // Tested with Postman (http://localhost:5000/api/v1/recipes/addLike?recipeId=1&userId=1)
    @PostMapping(value="/addLike", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe addLike(@RequestParam int recipeId, @RequestParam int userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            return null;
        }
        RecipeUserPreferencesService recipeUserPreferencesService = new RecipeUserPreferencesService(recipeUserPreferencesRepository);
        RecipeUserPreferences preferences = recipeUserPreferencesService.getPreferencesById(userId);
        if (preferences == null) {
            preferences = new RecipeUserPreferences(userId, null, null, null);
        }

        preferences.addLikedRecipe(recipeId);
        recipeUserPreferencesService.savePreferences(preferences);

        return recipeUserPreferencesService.getPreferencesById(userId).getLikedRecipes().contains(recipeId) ? recipe : null;
    }


    // Tested with Postman (http://localhost:5000/api/v1/recipes/removeLike?recipeId=1&userId=1)
    @DeleteMapping(value="/removeLike", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeLike(@RequestParam int recipeId, @RequestParam int userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            return false;
        }
        RecipeUserPreferencesService recipeUserPreferencesService = new RecipeUserPreferencesService(recipeUserPreferencesRepository);
        RecipeUserPreferences preferences = recipeUserPreferencesService.getPreferencesById(userId);
        if (preferences == null) {
            preferences = new RecipeUserPreferences(userId, null, null, null);
        }
        preferences.removeLikedRecipe(recipeId);
        recipeUserPreferencesService.savePreferences(preferences);

        return !recipeUserPreferencesService.getPreferencesById(userId).getLikedRecipes().contains(recipeId);
    }


    // Tested with Postman: http://localhost:5000/api/v1/recipes/addView?recipeId=1&userId=1
    @PostMapping(value="/addView", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe addView(@RequestParam int recipeId, @RequestParam int userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            return null;
        }
        RecipeUserPreferencesService recipeUserPreferencesService = new RecipeUserPreferencesService(recipeUserPreferencesRepository);
        RecipeUserPreferences preferences = recipeUserPreferencesService.getPreferencesById(userId);
        if (preferences == null) {
            preferences = new RecipeUserPreferences(userId, null, null, null);
        }

        preferences.addViewedRecipe(recipeId);
        recipeUserPreferencesService.savePreferences(preferences);

        return recipeUserPreferencesService.getPreferencesById(userId).getViewedRecipes().contains(recipeId) ? recipe : null;
    }


    @PostMapping(value = "/addHistory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe addHistory(@RequestParam int recipeId, @RequestParam int userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
        if (recipe == null) {
            return null;
        }
        RecipeUserPreferencesService recipeUserPreferencesService = new RecipeUserPreferencesService(recipeUserPreferencesRepository);
        RecipeUserPreferences preferences = recipeUserPreferencesService.getPreferencesById(userId);
        if (preferences == null) {
            preferences = new RecipeUserPreferences(userId, null, null, null);
        }

        preferences.addHistory(recipeId);
        recipeUserPreferencesService.savePreferences(preferences);

        return recipeUserPreferencesService.getPreferencesById(userId).getHistory().contains(recipeId) ? recipe : null;
    }

//    @GetMapping("/AIRecipeRecommendation/{title}")
//    public Recipe getRecipeRecommendation(@PathVariable String title) {
//        return recipeService.getAIRecipeRecommendation(title);
//    }


    /**
     *
     *                @front-team, I can set them to the default ones in case of wrong values, let me know if that helps u more; please read below!
     * @param pageDTO : Integer pageNo, Integer pageSize, Sort.Direction sortDirection {Sort.Direction.ASC, Sort.Direction.DESC}, String sortByField <nameOfRecipePropriety>
     *                defaults: pageNo = 0, pageSize = 10, sortDirection = Sort.Direction.ASC, sortByField = "recipeId"
     *                if you only give some of the parameters, the rest will be set to the default values! <3
     *                if you give invalid values for Sort.Direction or sortByField, it'll crash(for sortByField, you should get error code 500)
     * @return Page<Recipe>
     */

        // Tested with Postman (http://localhost:5000/api/v1/recipes/recipePage?pageNo=0&pageSize=10)
    //    {
    //        "pageNo" = 0
    //    }
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping("/recipePage")
    public Page<Recipe> getRecipePage(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(defaultValue = "recipeId") String sortByField) {
        // Validate sortByField
        if (!Arrays.asList("recipeId", "recipeTitle", "authorId", "authorName").contains(sortByField)) {
            throw new IllegalArgumentException("Invalid sortByField parameter");
        }

        // Create Pageable
        //  ...
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortDirection, sortByField);

        // Return the page of recipes
        return recipeRepository.findAll(pageable);
    }


    // Tested with Postman (http://localhost:5000/api/v1/recipes/recipePageByUserId?userId=1&pageNo=0&pageSize=10)
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping(value="/recipePageByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Recipe> getRecipeByUserId(@RequestParam int userId,
                                          @RequestParam(defaultValue = "0") int pageNo,
                                          @RequestParam(defaultValue = "10") int pageSize,
                                          @RequestParam(defaultValue = "ASC") Sort.Direction sortDirection,
                                          @RequestParam(defaultValue = "recipeId") String sortByField) {
        PageDTO pageDTO = new PageDTO(pageNo, pageSize, sortDirection, sortByField);
        Pageable page = pageDTO.getPageable(pageDTO);
        return recipeRepository.findRecipeByAuthorId(userId, page);
    }

    //so i don't accidentally set up again; will b uncommented when ill b working with db population again
    /*
    @GetMapping("/setupDB")
    public String setupDB() {
        try {
            recipeService.importDB();
        } catch (Exception e) {
            e.printStackTrace();
            return "DB setup failed!";
        }

        return "DB setup done!";
    }*/

    /**
     *          @front-team, I can modify the method for specific stuff, lemme know if u need anything else
     * @param keyword : the keyword to look for in a recipes category or keywords; the search is case-insensitive, and it searches for the given keywords in the category string(thus if the category contains a substring of the keyword, it'll be found) and in the keywords list, in the same manner(if any keyword in the list contains the given keyword as a substring, it'll be found)
     * @param pageDTO: Integer pageNo, Integer pageSize, Sort.Direction sortDirection {Sort.Direction.ASC, Sort.Direction.DESC}, String sortByField <nameOfRecipePropriety>
     *      *                defaults: pageNo = 0, pageSize = 10, sortDirection = Sort.Direction.ASC, sortByField = "recipeId"
     *      *                if you only give some of the parameters, the rest will be set to the default values! <3
     *      *                if you give invalid values for Sort.Direction or sortByField, it'll crash(for sortByField, you should get error code 500)
     * @return Page<Recipe>
     */

    // Tested with Post (http://localhost:5000/api/v1/recipes/recipePageByKeyword?keyword=Lunch)
    // {
    // }
    @CrossOrigin(origins = "http://localhost:3000") // Replace with your React app's URL
    @GetMapping(value = "/recipePageByKeyword", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Recipe> getRecipeByCategory(@RequestParam String keyword, @RequestBody PageDTO pageDTO) {
        Pageable page = pageDTO.getPageable(pageDTO);
        return recipeRepository.findRecipeByCategoryOrKeywordsQuery(keyword, page);
    }

}
