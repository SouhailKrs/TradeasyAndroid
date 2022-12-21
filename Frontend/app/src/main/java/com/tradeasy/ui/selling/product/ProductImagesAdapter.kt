package com.tradeasy.ui.selling.product

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.ProductImageItemBinding
// create a list of images
class ProductImagesAdapter(private val images: MutableList<Uri>) : RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = images[position]
        holder.bind(currentItem)
    }
    override fun getItemCount(): Int {
        return images.size
    }
    class MyViewHolder(val binding : ProductImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Uri) {
            binding.productImage.setImageURI(image)
        }
    }
    // add upload image function

}
