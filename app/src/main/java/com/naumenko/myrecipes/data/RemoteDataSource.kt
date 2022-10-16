package com.naumenko.myrecipes.data

import com.naumenko.myrecipes.data.network.FoodRecipeApi
import com.naumenko.myrecipes.domain.models.FoodJoke
import com.naumenko.myrecipes.domain.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipeApi: FoodRecipeApi
) {

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipeApi.getRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe> {
        return foodRecipeApi.searchRecipes(searchQuery)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipeApi.getFoodJoke(apiKey)
    }

}