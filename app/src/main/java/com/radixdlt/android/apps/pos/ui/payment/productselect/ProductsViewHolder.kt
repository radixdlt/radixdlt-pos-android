package com.radixdlt.android.apps.pos.ui.payment.productselect

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.radixdlt.android.apps.pos.R
import com.radixdlt.client.application.translate.data.receipt.PurchasableArticle

class ProductsViewHolder(
    item: View,
    private val priceView: TextView = item.findViewById(R.id.rank),
    private val productView: ImageView = item.findViewById(R.id.image),
    private val nameView: TextView = item.findViewById(R.id.name),
    private val itemClick: (PurchasableArticle) -> Unit
) : RecyclerView.ViewHolder(item) {

    fun bind(product: PurchasableArticle) { // price: String, productName: String, productImageUrl: String
        priceView.text = "$${product.price}"
        nameView.text = product.name
        Glide.with(productView)
            .load(product.imageUrl.get())
            .transition(withCrossFade())
            .into(productView)

        itemView.setOnClickListener {
            itemClick(product)
        }
    }
}
