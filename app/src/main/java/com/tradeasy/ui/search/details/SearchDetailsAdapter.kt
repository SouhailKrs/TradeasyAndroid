package com.tradeasy.ui.search.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.SearchDetailsItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.imageLoader

class SearchDetailsAdapter(
    private val products: MutableList<Product>,
    val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<SearchDetailsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            SearchDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


    class MyViewHolder(val binding: SearchDetailsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.prodName.text = product.name
            binding.prodCategory.text = product.category
            binding.prodPrice.text = product.price.toString()
            imageLoader(product.image!![0],binding.prodImage)
        }


    }
}