package com.restservice.recipeAndMealPlanning.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;

@Service
public class RecommendationSystem {
    private static final double VIEWED_RECIPES_WEIGHT = 0.2;
    private static final double LIKED_RECIPES_WEIGHT = 0.5;
    private static final double HISTORY_WEIGHT = 0.3;

    private static final int POPULAR_RECIPES_LIMIT = 100;

    private static final int NUM_RECOMMENDATIONS = 21;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    public RecommendationSystem(RecipeService recipeService, RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
    }

    public Map<Integer, List<Integer>> getAllRecommendations() {
        Map<Integer, List<Integer>> allRecommendations = new HashMap<>();
        Map<Integer, List<Integer>> userInteractions = loadUserInteractions();

        for (Map.Entry<Integer, List<Integer>> entry : userInteractions.entrySet()) {
            int targetUserId = entry.getKey();
            Map<Integer, Double> similarityScores = calculateSimilarityScores(targetUserId, userInteractions);
            List<Integer> recommendations = recommendItems(targetUserId, userInteractions, similarityScores);
            allRecommendations.put(targetUserId, recommendations);
        }

        return allRecommendations;
    }

    public Map<String, List<Integer>> categorizeRecommendationsForUser(List<Integer> userRecommendations) { //Map<String, Recipe> userRecommendations) {
        Map<String, List<Integer>> categorizedRecommendations = new HashMap<>();
        List<Integer> breakfastRecipes = new ArrayList<>();
        List<Integer> lunchRecipes = new ArrayList<>();
        List<Integer> otherRecipes = new ArrayList<>();

        for (Integer recipeId : userRecommendations) {
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe != null) {
                String mealCategory = recipe.getCategory().toLowerCase();
                List<String> keywords = recipe.getKeywords();
                if (mealCategory.contains("breakfast") || containsKeyword(keywords, "breakfast")) {
                    breakfastRecipes.add(recipeId);
                } else if (mealCategory.contains("lunch") || containsKeyword(keywords, "lunch")) {
                    lunchRecipes.add(recipeId);
                } else {
                    otherRecipes.add(recipeId);
                }
            }
        }

        categorizedRecommendations.put("breakfast", breakfastRecipes);
        categorizedRecommendations.put("lunch", lunchRecipes);
        categorizedRecommendations.put("other", otherRecipes);

        return categorizedRecommendations;
    }

    private boolean containsKeyword(List<String> keywords, String keyword) {
        for (String kw : keywords) {
            if (kw.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // TODO REDO THIS SO IT TAKES THE ACTUAL PREFERENCES USING METHODS FROM THE RecipeUserPreferences
    // load user interactions from database
    public Map<Integer, List<Integer>> loadUserInteractions() {
        Map<Integer, List<Integer>> userInteractions = new HashMap<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://shms-db.cv8ou2i408tg.eu-north-1.rds.amazonaws.com:3306/ipSHMS", "admin", "parolacomplicata11")) {
            String query = "SELECT * FROM recipe_user_preferences";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int userId = resultSet.getInt("id");
                    List<Integer> viewedRecipes = parseRecipeIds(resultSet.getString("ViewedRecipes"));
                    List<Integer> likedRecipes = parseRecipeIds(resultSet.getString("LikedRecipes"));
                    List<Integer> history = parseRecipeIds(resultSet.getString("History"));
                    List<Integer> combinedInteractions = combineInteractions(viewedRecipes, likedRecipes, history);
                    userInteractions.put(userId, combinedInteractions);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userInteractions;
    }

    // Calculate similarity scores
    private static Map<Integer, Double> calculateSimilarityScores(int targetUserId, Map<Integer, List<Integer>> userInteractions) {
        Map<Integer, Double> similarityScores = new HashMap<>();

        for (Map.Entry<Integer, List<Integer>> entry : userInteractions.entrySet()) {
            int userId = entry.getKey();
            List<Integer> interactions = entry.getValue();

            if (userId != targetUserId) {
                double similarity = calculateCosineSimilarity(userInteractions.get(targetUserId), interactions);
                similarityScores.put(userId, similarity);
            }
        }

        return similarityScores;
    }

    // Calculate cosine similarity
    private static double calculateCosineSimilarity(List<Integer> interactions1, List<Integer> interactions2) {
        Set<Integer> set1 = new HashSet<>(interactions1);
        Set<Integer> set2 = new HashSet<>(interactions2);

        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);

        return (double) intersection.size() / union.size();
    }

    // Recommend items to a user
    private List<Integer> recommendItems(int targetUserId, Map<Integer, List<Integer>> userInteractions, Map<Integer, Double> similarityScores) {
        Map<Integer, Double> itemScores = new HashMap<>();
        List<Integer> targetUserInteractions = userInteractions.get(targetUserId);

        for (Map.Entry<Integer, List<Integer>> entry : userInteractions.entrySet()) {
            int userId = entry.getKey();
            List<Integer> interactions = entry.getValue();

            if (userId != targetUserId) {
                double similarityScore = similarityScores.get(userId);

                for (Integer itemId : interactions) {
                    if (!targetUserInteractions.contains(itemId)) { // Only consider items that the target user has not interacted with
                        double oldScore = itemScores.getOrDefault(itemId, 0.0);
                        double newScore = oldScore + similarityScore; // This is a simple sum, you might want to use a different calculation
                        itemScores.put(itemId, newScore);
                    }
                }
            }
        }

        List<Map.Entry<Integer, Double>> itemScoreList = new ArrayList<>(itemScores.entrySet());
        itemScoreList.sort(Map.Entry.comparingByValue(Collections.reverseOrder()));

        List<Integer> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : itemScoreList) {
            if (recommendations.size() < NUM_RECOMMENDATIONS) { // Only add the top NUM_RECOMMENDATIONS items
                recommendations.add(entry.getKey());
            } else {
                break;
            }
        }

        // If less than NUM_RECOMMENDATIONS recommendations, consider lower rated recipes from combinedInteractions
        if (recommendations.size() < NUM_RECOMMENDATIONS) {
            List<Integer> lowerRatedRecipes = new ArrayList<>(targetUserInteractions);
            lowerRatedRecipes.removeAll(itemScores.keySet()); // Remove already recommended items
            Collections.shuffle(lowerRatedRecipes); // Shuffle to add randomness
            for (Integer recipe : lowerRatedRecipes) {
                if (recommendations.size() < NUM_RECOMMENDATIONS) {
                    recommendations.add(recipe);
                } else {
                    break;
                }
            }
        }

        // If still less than NUM_RECOMMENDATIONS recommendations, add popular recipes
        if (recommendations.size() < NUM_RECOMMENDATIONS) {
            List<Integer> popularRecipes = loadPopularRecipes();
            for (Integer recipe : popularRecipes) {
                if (!recommendations.contains(recipe)) {
                    recommendations.add(recipe);
                }
                if (recommendations.size() >= NUM_RECOMMENDATIONS) {
                    break;
                }
            }
        }

        return recommendations;
    }

    // Parse recipe IDs from a string
    private static List<Integer> parseRecipeIds(String recipeIdsStr) {
        List<Integer> recipeIds = new ArrayList<>();
        if (recipeIdsStr != null && !recipeIdsStr.isEmpty() && !recipeIdsStr.equals("[]")) {
            // Remove the square brackets and then split the string
            String[] recipeIdsArray = recipeIdsStr.replace("[", "").replace("]", "").trim().split("\\s*,\\s*");
            for (String id : recipeIdsArray) {
                try {
                    recipeIds.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in string: " + id);
                }
            }
        }
        return recipeIds;
    }

    // Combine interactions
    private static List<Integer> combineInteractions(List<Integer> viewedRecipes, List<Integer> likedRecipes, List<Integer> history) {
        List<Integer> combinedInteractions = new ArrayList<>();
        combinedInteractions.addAll(viewedRecipes);
        combinedInteractions.addAll(likedRecipes);
        combinedInteractions.addAll(history);

        Map<Integer, Double> weights = new HashMap<>();
        for (Integer recipeId : viewedRecipes) {
            weights.put(recipeId, weights.getOrDefault(recipeId, 0.0) + VIEWED_RECIPES_WEIGHT);
        }
        for (Integer recipeId : likedRecipes) {
            weights.put(recipeId, weights.getOrDefault(recipeId, 0.0) + LIKED_RECIPES_WEIGHT);
        }
        for (Integer recipeId : history) {
            weights.put(recipeId, weights.getOrDefault(recipeId, 0.0) + HISTORY_WEIGHT);
        }

        Set<Integer> uniqueRecipes = new HashSet<>(combinedInteractions);
        combinedInteractions.clear();
        combinedInteractions.addAll(uniqueRecipes);

        combinedInteractions.sort((recipeId1, recipeId2) -> Double.compare(weights.get(recipeId2), weights.get(recipeId1)));

        return combinedInteractions;
    }

    // Load popular recipes (higher review count) from database as backup for the meal plan - for inactive users
    private List<Integer> loadPopularRecipes() {
        List<Integer> popularRecipes = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://shms-db.cv8ou2i408tg.eu-north-1.rds.amazonaws.com:3306/ipSHMS", "admin", "parolacomplicata11")) {
            {
                String query = "SELECT id FROM recipes ORDER BY review_count DESC LIMIT " + POPULAR_RECIPES_LIMIT;
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {

                    while (resultSet.next()) {
                        popularRecipes.add(resultSet.getInt("id"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return popularRecipes;
    }

    // Calculate the similarity between two users based on their interactions
    public static Map<List<Integer>, Double> calculateAllSimilarities(Map<Integer, List<Integer>> userInteractions) {
        Map<List<Integer>, Double> allSimilarities = new HashMap<>();

        List<Integer> userIds = new ArrayList<>(userInteractions.keySet());
        for (int i = 0; i < userIds.size(); i++) {
            for (int j = i + 1; j < userIds.size(); j++) {
                int userId1 = userIds.get(i);
                int userId2 = userIds.get(j);
                double similarity = calculateCosineSimilarity(userInteractions.get(userId1), userInteractions.get(userId2));
                allSimilarities.put(Arrays.asList(userId1, userId2), similarity);
            }
        }

        return allSimilarities;
    }
//
//
//    /// Transform into an actual meal plan based on Breakfast, Lunch, Dinner
//
//    // Generate meal plans for each day of the week
//    public Map<DayOfWeek, List<Integer>> generateMealPlans(Map<Integer, List<Integer>> userInteractions, Map<Integer, List<String>> userMealPreferences) {
//        Map<DayOfWeek, List<Integer>> mealPlans = new HashMap<>();
//        for (DayOfWeek day : DayOfWeek.values()) {
//            List<String> mealPreferences = userMealPreferences.getOrDefault(day, Collections.emptyList());
//            List<Integer> recommendations = generateRecommendationsForDay(userInteractions, mealPreferences);
//            mealPlans.put(day, recommendations);
//        }
//        return mealPlans;
//    }
//
//    // Generate recommendations for a specific day based on user meal preferences
//    private List<Integer> generateRecommendationsForDay(Map<Integer, List<Integer>> userInteractions, List<String> mealPreferences) {
//        List<Integer> recommendations = new ArrayList<>();
//        for (String mealPreference : mealPreferences) {
//            List<Integer> recipes = getRecipesByMealType(mealPreference);
//            recommendations.addAll(recommendItemsByMealType(userInteractions, recipes));
//        }
//        return recommendations.subList(0, Math.min(recommendations.size(), NUM_RECOMMENDATIONS));
//    }
//
//    private List<Integer> getRecipesByMealType(String mealPreference) {
//        List<Integer> recipes = new ArrayList<>();
//        try {
//            Pageable pageable = PageRequest.of(0, NUM_RECOMMENDATIONS); // Assuming NUM_RECOMMENDATIONS is the desired limit
//            Page<Recipe> recipePage = recipeRepository.findRecipeByCategoryOrKeywordsQuery(mealPreference, pageable);
//            List<Recipe> recipeList = recipePage.getContent();
//
//            for (Recipe recipe : recipeList) {
//                recipes.add(recipe.getRecipeId()); // Assuming getId() returns the recipe Id
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return recipes;
//    }
//
//    private Collection<Integer> recommendItemsByMealType(Map<Integer, List<Integer>> userInteractions, List<Integer> recipes) {
//        // Implement logic to retrieve recipes by meal type from your database
//
//
//        return null;
//    }

    private List<Integer> getRecipesByMealType(String mealPreference) {
        List<Integer> recipes = new ArrayList<>();
        try {
            Pageable pageable = PageRequest.of(0, NUM_RECOMMENDATIONS); // Assuming NUM_RECOMMENDATIONS is the desired limit
            Page<Recipe> recipePage = recipeRepository.findRecipeByCategoryOrKeywordsQuery(mealPreference, pageable);
            List<Recipe> recipeList = recipePage.getContent();

            for (Recipe recipe : recipeList) {
                recipes.add(recipe.getRecipeId()); // Assuming getId() returns the recipe Id
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recipes;
    }


}


