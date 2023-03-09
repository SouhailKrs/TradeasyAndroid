package com.tradeasy.ui.home.forBid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.ForBidItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.getScreenSize
import com.tradeasy.utils.imageLoader
import java.util.*

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
            binding.productNameTextView.text = product.name!!.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
            binding.productPriceTextView.text = product.price.toString() + " TND"
            val productImage= binding.productImageView
            binding.prodCardView.layoutParams.height = (getScreenSize(binding.root.context).first*0.30).toInt()
            binding.productImageView.layoutParams.height = (getScreenSize(binding.root.context).first*0.14).toInt()
            binding.prodCardView.layoutParams.width = (getScreenSize(binding.root.context).second * 0.40).toInt()


            imageLoader(product.image!![0],productImage)

        }


    }
}