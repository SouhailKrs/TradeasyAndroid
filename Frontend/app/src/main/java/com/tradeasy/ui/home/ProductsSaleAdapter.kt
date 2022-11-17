package com.tradeasy.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.R

class ProductsSaleAdapter(private val productsList: ArrayList<Products>) : RecyclerView.Adapter<ProductsSaleAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.row_happyhour, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.productTitle.text = currentItem.title
        holder.productDescription.text = currentItem.description
        holder.productPrice.text = currentItem.price.toString()
        holder.productImage.setImageResource(currentItem.image)
    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_img)
        val productTitle: TextView = itemView.findViewById(R.id.product_title)
        val productDescription: TextView = itemView.findViewById(R.id.product_description)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)

    }
}