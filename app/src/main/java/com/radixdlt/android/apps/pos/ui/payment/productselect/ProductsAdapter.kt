package com.radixdlt.android.apps.pos.ui.payment.productselect

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.radixdlt.android.apps.pos.R
import com.radixdlt.client.application.translate.data.receipt.PurchasableArticle

internal class ProductsAdapter(
    private val items: MutableList<PurchasableArticle> = mutableListOf(),
    private val itemClick: (PurchasableArticle) -> Unit
) : RecyclerView.Adapter<ProductsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder =
        ProductsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false),
            itemClick = itemClick
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        items[position].also {
            holder.bind(it)
        }
    }
}
