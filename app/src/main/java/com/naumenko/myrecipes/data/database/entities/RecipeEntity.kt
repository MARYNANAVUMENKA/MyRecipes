package com.naumenko.myrecipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.naumenko.myrecipes.domain.models.FoodRecipe
import com.naumenko.myrecipes.util.Constants.Companion.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipeEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}