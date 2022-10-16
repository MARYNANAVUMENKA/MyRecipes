package com.naumenko.myrecipes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.naumenko.myrecipes.R
import com.naumenko.myrecipes.databinding.RecipesRowLayoutBinding
import com.naumenko.myrecipes.domain.models.Result
import org.jsoup.Jsoup

interface ResultActionListener {
    fun onUserDetails(result: Result)
}

class UsersDiffCallback(
    private val oldList: List<Result>,
    private val newList: List<Result>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldResult = oldList[oldItemPosition]
        val newResult = newList[newItemPosition]
        return oldResult.recipeId == newResult.recipeId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }
}

class RecipesAdapter(
    private val actionListener: ResultActionListener
) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>(), View.OnClickListener {

    var recipes: List<Result> = emptyList()
        set(newValue) {
            val diffCallback = UsersDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onClick(v: View) {
        val result = v.tag as Result
        actionListener.onUserDetails(result)

    }

    override fun getItemCount(): Int = recipes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipesRowLayoutBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)
        return RecipesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipe = recipes[position]

        val context = holder.itemView.context
        with(holder.binding) {
            holder.itemView.tag = currentRecipe
            titleTextView.text = currentRecipe.title

            if (currentRecipe.summary != null) {
                val desc = Jsoup.parse(currentRecipe.summary).text()
                descriptionTextView.text = desc
            }
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