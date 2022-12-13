package com.tradeasy.ui.profile.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.SavedProductItemBinding
import com.tradeasy.domain.model.Product
import com.tradeasy.utils.ImageLoader

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

            ImageLoader(product.image!!,productImage)

        }


    }
}