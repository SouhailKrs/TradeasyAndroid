package com.tradeasy.ui.profile.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.SavedProductItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.getScreenSize
import com.tradeasy.utils.imageLoader

class SavedProductsAdapter(
    private val products: MutableList<Product>,
    val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<SavedProductsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            SavedProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = products[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
        // holder.productImage.setImageResource(currentItem.image!!.toInt())
    }

    override fun getItemCount(): Int {
        return products.size
    }


    fun updateList(mProducts: List<Product>) {
        products.clear()
        products.addAll(mProducts)
        notifyDataSetChanged()
    }


    class MyViewHolder(val binding: SavedProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.savedProdName.text = product.name
            binding.savedProdPrice.text = product.price.toString()
            binding.SavedProdCategory.text = product.category
            val productImage= binding.SavedProductImageView
            binding.imgCardView.layoutParams.height = (getScreenSize(binding.root.context).first*0.17).toInt()
            binding.imgCardView.layoutParams.width = (getScreenSize(binding.root.context).second * 0.28).toInt()
            imageLoader(product.image!![0],productImage)

        }


    }
}