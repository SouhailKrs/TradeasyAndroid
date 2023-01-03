package com.tradeasy.ui.selling.product

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.ProductImageItemBinding
import com.tradeasy.utils.getScreenSize

// create a list of images
        class ProductImagesAdapter( val images: MutableList<Uri>   ) : RecyclerView.Adapter<ProductImagesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ProductImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int,) {

        val currentItem = images[position]
        holder.bind(currentItem)
        // remove item from list when clickig on delete button
        holder.binding.deleteImgBtn.setOnClickListener {
            images.removeAt(position)
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return images.size
    }
     class MyViewHolder(val binding : ProductImageItemBinding) : RecyclerView.ViewHolder(binding.root) {



        fun bind(image: Uri) {
            binding.productImage.setImageURI(image)
            println("image uri is $image")
            binding.additionalInfoCardView.layoutParams.height = (getScreenSize(binding.root.context).first*0.20).toInt()
            binding.productImage.layoutParams.height = (getScreenSize(binding.root.context).first*0.14).toInt()
            binding.additionalInfoCardView.layoutParams.width = (getScreenSize(binding.root.context).second * 0.35).toInt()


        }


    }
    // add upload image function


}
