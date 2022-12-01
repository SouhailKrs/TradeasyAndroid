package com.tradeasy.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.R
import com.tradeasy.domain.model.Product

class SearchAdapter(private val products: MutableList<Product>) : RecyclerView.Adapter<SearchAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
        return SearchAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SearchAdapter.MyViewHolder, position: Int) {
        val currentItem = products[position]
        holder.productTitle.text = currentItem.name
        holder.productDescription.text = currentItem.description


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

        val productTitle: TextView = itemView.findViewById(R.id.searchedProductName)!!
        val productDescription: TextView = itemView.findViewById(R.id.searchedProductDescription)

    }
}