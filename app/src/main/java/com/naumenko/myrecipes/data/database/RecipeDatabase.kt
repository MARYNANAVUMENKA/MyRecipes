package com.naumenko.myrecipes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.naumenko.myrecipes.data.database.entities.FavoriteEntity
import com.naumenko.myrecipes.data.database.entities.FoodJokeEntity
import com.naumenko.myrecipes.data.database.entities.RecipeEntity

@Database(
    entities = [RecipeEntity::class, FavoriteEntity::class, FoodJokeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecipeTypeConverter::class)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipeDao

}