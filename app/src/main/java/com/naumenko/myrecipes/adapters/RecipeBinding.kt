package com.naumenko.myrecipes.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.naumenko.myrecipes.data.database.entities.RecipeEntity
import com.naumenko.myrecipes.domain.models.FoodRecipe
import com.naumenko.myrecipes.util.NetworkResult

class RecipeBinding {

    companion object {

        @BindingAdapter("readApiResponse","readDatabase",requireAll = true)
        @JvmStatic
        fun handleReadDataErrors(
            view: View,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipeEntity>?
        ){
            when (view){
                is ImageView ->{
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView ->{
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }

}