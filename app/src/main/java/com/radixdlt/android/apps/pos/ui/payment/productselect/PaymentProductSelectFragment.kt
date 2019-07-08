package com.radixdlt.android.apps.pos.ui.payment.productselect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentViewModel
import com.radixdlt.client.application.translate.data.receipt.PurchasableArticle
import kotlinx.android.synthetic.main.fragment_payment_products_grid.*

class PaymentProductSelectFragment : BaseFragment() {

    private lateinit var productsAdapter: ProductsAdapter

    private val paymentViewModel: PaymentViewModel by activityViewModels()

    private val purchasableArticles = mutableListOf(
        PurchasableArticle.brownie(),
        PurchasableArticle.croissant(),
        PurchasableArticle.fineGroundCoffee(),
        PurchasableArticle.macaron()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_products_grid, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        paymentViewModel.createNewPurchase()
        initialiseViews()
        initialiseGrid()
    }

    private fun initialiseViews() {
        initialiseGrid()
        setContinueOnClickListener()
    }

    private fun setContinueOnClickListener() {
        paymentProductSelectContinueButton.setOnClickListener {
            val action = PaymentProductSelectFragmentDirections
                .actionNavigationPaymentProductsGridToNavigationPaymentTapScan()
            findNavController().navigate(action)
        }
    }

    private fun initialiseGrid() {
        productsAdapter = ProductsAdapter(purchasableArticles, click)
        productsGrid.apply {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        }
    }

    private val click = fun(product: PurchasableArticle) {
        paymentViewModel.purchase.addArticle(product)

        totalPriceTextView.text = getString(
            R.string.fragment_payment_product_select_total_price,
            paymentViewModel.purchase.costOfArticles().toString()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_payment_close, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_quit_payment -> {
                findNavController().navigate(R.id.navigation_payment_cancel)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
