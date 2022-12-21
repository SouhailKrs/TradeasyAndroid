package com.tradeasy.ui.home.forBid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.R
import com.tradeasy.databinding.ForBidItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.ImageLoader

class ProductsForBid(private val products: MutableList<Product>, val onItemClick:(Product)->Unit) : RecyclerView.Adapter<ProductsForBid.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            ForBidItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = products[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener{
            onItemClick(currentItem)
        }
       // holder.productImage.setImageResource(currentItem.image!!.toInt())
    }

    override fun getItemCount(): Int {
        return products.size
    }
    fun updateList(mProducts: List<Product>){
        products.clear()
        products.addAll(mProducts)
        notifyDataSetChanged()
    }


    class MyViewHolder(val binding : ForBidItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
       binding.productImageView.setImageResource(R.drawable.ic_baseline_bookmark_24)
            binding.productNameTextView.text = product.name
            binding.productPriceTextView.text = product.price.toString()
            val productImage= binding.productImageView
ImageLoader(product.image!!,productImage)

        }


    }
}