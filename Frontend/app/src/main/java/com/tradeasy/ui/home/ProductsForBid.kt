package com.tradeasy.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.tradeasy.R
import com.tradeasy.domain.model.Product

class ProductsForBid(private val products: MutableList<Product>, val onItemClick:(Product)->Unit) : RecyclerView.Adapter<ProductsForBid.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsForBid.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bidding_item, parent, false)
        return ProductsForBid.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsForBid.MyViewHolder, position: Int) {
        val currentItem = products[position]
        holder.productTitle.text = currentItem.name
        holder.productDescription.text = currentItem.description
        holder.productPrice.text = currentItem.price.toString()
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


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage : ShapeableImageView = itemView.findViewById(R.id.productImageView)
        val productTitle: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)

    }
}