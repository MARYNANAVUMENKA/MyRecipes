package com.naumenko.myrecipes.adapters

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.naumenko.myrecipes.data.database.entities.FavoriteEntity

class FavoritesRecipesBinding {

    companion object {

        @BindingAdapter("setVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setVisibility(view: View, favoriteEntity: List<FavoriteEntity>?, mAdapter: FavoritesRecipesAdapter?) {
            when (view) {
                is RecyclerView -> {
                    val dataCheck = favoriteEntity.isNullOrEmpty()
                    view.isInvisible = dataCheck
                    if(!dataCheck){
                        favoriteEntity?.let { mAdapter?.setData(it) }
                    }
                }
                else -> view.isVisible = favoriteEntity.isNullOrEmpty()
            }
        }

    }

}