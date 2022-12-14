package com.tradeasy.ui.home.forBid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.tradeasy.R
import com.tradeasy.domain.product.entity.Product

class ProductsAdapter(private val productsList: MutableList<Product>) : RecyclerView.Adapter<ProductsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recently_viewed_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = productsList[position]
        holder.productTitle.text = currentItem.name
        holder.productDescription.text = currentItem.description
        holder.productPrice.text = currentItem.price.toString()
        //holder.productImage.setImageResource(currentItem.image)


    }

    override fun getItemCount(): Int {
        return productsList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val productImage : ShapeableImageView = itemView.findViewById(R.id.productImageView)
        val productTitle: TextView = itemView.findViewById(R.id.productName)
        val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)

    }


}