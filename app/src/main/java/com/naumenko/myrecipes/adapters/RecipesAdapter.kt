package com.naumenko.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.naumenko.myrecipes.R
import com.naumenko.myrecipes.databinding.RecipesRowLayoutBinding
import com.naumenko.myrecipes.domain.models.Result
import org.jsoup.Jsoup

interface ResultActionListener {
    fun onRecipesDetails(result: Result)
}

class RecipesAdapter(
    private val actionListener: ResultActionListener
) : ListAdapter<Result, RecipesAdapter.RecipesViewHolder>(ItemCallback), View.OnClickListener {


    override fun onClick(v: View) {
        val result = v.tag as Result
        actionListener.onRecipesDetails(result)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipesRowLayoutBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipe = getItem(position)

        with(holder.binding) {
            root.tag = currentRecipe
            titleTextView.text = currentRecipe.title
            descriptionTextView.text = Jsoup.parse(currentRecipe.summary).text()
            heartTextView.text = currentRecipe.aggregateLikes.toString()
            clockTextView.text = currentRecipe.readyInMinutes.toString()
            if (currentRecipe.vegan) {
                leafImageView.setColorFilter(
                    ContextCompat.getColor(
                        leafImageView.context,
                        R.color.green
                    )
                )
                leafTextView.setTextColor(
                    ContextCompat.getColor(
                        leafTextView.context,
                        R.color.green
                    )
                )
            }

            if (currentRecipe.image.isNotBlank()) {
                Glide.with(recipeImageView.context)
                    .load(currentRecipe.image)
                    .centerCrop()
                    .into(recipeImageView)
            } else {
                Glide.with(recipeImageView.context).clear(recipeImageView)
                recipeImageView.setImageResource(R.drawable.ic_error_placeholder)
            }
        }
    }


    class RecipesViewHolder(
        val binding: RecipesRowLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root)

}

object ItemCallback : DiffUtil.ItemCallback<Result>() {

    override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem.recipeId == newItem.recipeId
    }

    override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
        return oldItem == newItem
    }
}
