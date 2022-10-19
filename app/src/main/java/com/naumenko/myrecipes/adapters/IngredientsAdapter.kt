package com.naumenko.myrecipes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.naumenko.myrecipes.R
import com.naumenko.myrecipes.databinding.IngredientsRowLayoutBinding
import com.naumenko.myrecipes.domain.models.ExtendedIngredient
import com.naumenko.myrecipes.util.Constants.Companion.BASE_IMAGE_URL
import java.util.*


class IngredientsAdapter :
    ListAdapter<ExtendedIngredient, IngredientsAdapter.IngredientsViewHolder>(ItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IngredientsRowLayoutBinding.inflate(inflater, parent, false)
        return IngredientsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val currentIngredient = getItem(position)

        with(holder.binding) {
            root.tag = currentIngredient

            ingredientName.text = currentIngredient.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

            ingredientAmount.text = currentIngredient.amount.toString()
            ingredientUnit.text = currentIngredient.unit
            ingredientConsistency.text = currentIngredient.consistency
            ingredientOriginal.text = currentIngredient.original

            if (currentIngredient.image !== null) {
                Glide.with(ingredientImageView.context)
                    .load(BASE_IMAGE_URL + currentIngredient.image)
                    .fitCenter()
                    .into(ingredientImageView)
            } else {
                Glide.with(ingredientImageView.context).clear(ingredientImageView)
                ingredientImageView.setImageResource(R.drawable.ic_error_placeholder)
            }
        }
    }


    class IngredientsViewHolder(
        val binding: IngredientsRowLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root)

    object ItemCallback : DiffUtil.ItemCallback<ExtendedIngredient>() {

        override fun areItemsTheSame(
            oldItem: ExtendedIngredient,
            newItem: ExtendedIngredient
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ExtendedIngredient,
            newItem: ExtendedIngredient
        ): Boolean {
            return oldItem == newItem
        }
    }

}
