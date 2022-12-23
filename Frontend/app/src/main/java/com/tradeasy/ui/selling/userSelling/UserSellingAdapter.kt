package com.tradeasy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.UserSellingItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.ImageLoader

class UserSellingAdapter(private val products: MutableList<Product>, val onItemClick:(Product)->Unit) : RecyclerView.Adapter<UserSellingAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            UserSellingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


    class MyViewHolder(val binding : UserSellingItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {

            binding.userSellingName.text = product.name
            binding.userSellingPrice.text = product.price.toString()
            val productImage= binding.userSellingImg

            ImageLoader(product.image!![0],productImage)

        }


    }
}