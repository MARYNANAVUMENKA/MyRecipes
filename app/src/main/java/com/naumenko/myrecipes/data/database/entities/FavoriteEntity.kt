package com.naumenko.myrecipes.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.naumenko.myrecipes.domain.models.Result
import com.naumenko.myrecipes.util.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)