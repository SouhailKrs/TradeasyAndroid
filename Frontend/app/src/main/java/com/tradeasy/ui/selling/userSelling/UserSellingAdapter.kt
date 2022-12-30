package com.tradeasy.ui.selling.userSelling

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.UserProductItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.getScreenSize
import com.tradeasy.utils.imageLoader

class UserSellingAdapter(private val products: MutableList<Product>, val onItemClick:(Product)->Unit) : RecyclerView.Adapter<UserSellingAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            UserProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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


    class MyViewHolder(val binding : UserProductItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.userProdName.text = product.name
binding.prodPrice.text = product.price.toString() + " TND"
            val productImage= binding.userProductImg
            binding.userProdCardView.layoutParams.height = (getScreenSize(binding.root.context).first*0.24).toInt()
            binding.userProductImg.layoutParams.height = (getScreenSize(binding.root.context).first*0.18).toInt()
            binding.userProdCardView.layoutParams.width = (getScreenSize(binding.root.context).second * 0.38).toInt()
            imageLoader(product.image!![0],productImage)

        }


    }
}