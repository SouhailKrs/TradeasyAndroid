package com.tradeasy.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.SearchItemBinding
import com.tradeasy.domain.product.entity.Product

class SearchAdapter(
    private val products: MutableList<Product>,
    val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = products[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener{
            onItemClick(currentItem)
        }


    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateList(mProducts: List<Product>) {
        products.clear()
        products.addAll(mProducts)
        notifyDataSetChanged()
    }


    class MyViewHolder(val binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.searchItemName.text = product.name
            binding.searchItemCategory.text = product.category
        }


    }
}