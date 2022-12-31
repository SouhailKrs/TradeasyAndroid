package com.tradeasy.ui.selling.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.CategoryItemBinding
import com.tradeasy.domain.category.entity.Category
import java.util.*

class CategoriesAdapter(private val categories: MutableList<Category>, val onItemClick:(Category)->Unit) : RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = categories[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener{
            onItemClick(currentItem)
        }

    }

    override fun getItemCount(): Int {
        return categories.size
    }
    fun updateList(mCategory: List<Category>){
        categories.clear()
        categories.addAll(mCategory)
        notifyDataSetChanged()
    }


    class MyViewHolder(val binding : CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {

            binding.categoryName.text = category.name!!.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }

        }


    }
}