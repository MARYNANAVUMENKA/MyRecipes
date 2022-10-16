package com.naumenko.myrecipes.data

import com.naumenko.myrecipes.data.database.RecipeDao
import com.naumenko.myrecipes.data.database.entities.FavoriteEntity
import com.naumenko.myrecipes.data.database.entities.FoodJokeEntity
import com.naumenko.myrecipes.data.database.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipeDao: RecipeDao
) {

    fun readRecipes(): Flow<List<RecipeEntity>> {
        return recipeDao.readRecipes()
    }

    fun readFavoriteRecipes(): Flow<List<FavoriteEntity>> {
        return recipeDao.readFavoriteRecipes()
    }

    fun readFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipeDao.readFoodJoke()
    }

    suspend fun insertRecipes(recipeEntity: RecipeEntity) {
        recipeDao.insertRecipes(recipeEntity)
    }

    suspend fun insertFavoriteRecipes(favoriteEntity: FavoriteEntity) {
        recipeDao.insertFavoriteRecipe(favoriteEntity)
    }

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipeDao.insertFoodJoke(foodJokeEntity)
    }

    suspend fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity) {
        recipeDao.deleteFavoriteRecipe(favoriteEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipeDao.deleteAllFavoriteRecipes()
    }

}