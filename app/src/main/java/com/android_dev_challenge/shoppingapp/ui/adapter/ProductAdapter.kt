package com.android_dev_challenge.shoppingapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android_dev_challenge.shoppingapp.BR
import com.android_dev_challenge.shoppingapp.R
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto
import com.bumptech.glide.Glide

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var productList: List<ProductDto> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product: ProductDto = productList[position]

        holder.run {
            Glide.with(holder.productImageView.context)
                .load("http:${product.imageUrl}")
                .placeholder(R.drawable.placeholder_image)
                .fallback(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.productImageView)

            bind(product)
        }
    }

    override fun getItemCount() = productList.size

    override fun getItemViewType(position: Int) = R.layout.adapter_product_list_item

    class ProductViewHolder(
        private val binding: ViewDataBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val productImageView: ImageView = binding.root.findViewById(R.id.productImageView)

        fun bind(product: ProductDto) {
            binding.setVariable(BR.product, product)
            binding.executePendingBindings()
        }
    }
}