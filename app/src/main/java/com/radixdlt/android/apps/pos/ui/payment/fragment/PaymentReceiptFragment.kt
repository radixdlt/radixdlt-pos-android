package com.radixdlt.android.apps.pos.ui.payment.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentViewModel
import com.radixdlt.client.application.translate.data.receipt.Receipt
import com.radixdlt.client.application.translate.data.receipt.ReceiptItem
import kotlinx.android.synthetic.main.fragment_payment_receipt.*
import net.skoumal.fragmentback.BackFragment
import java.math.BigDecimal
import java.text.DateFormat

class PaymentReceiptFragment : Fragment(), BackFragment {

    private val args: PaymentReceiptFragmentArgs by navArgs()
    private val reference: String by lazy { args.reference ?: "" }

    private val paymentViewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_receipt, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateViewWith(paymentViewModel.purchase.receipt)
        setFinishButtonOnClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun populateViewWith(receipt: Receipt) {
        merchantNameTextView.text = receipt.merchant.name
        val cost = receipt.items.stream()
            .map { i -> i.calculateCost()}
            .reduce(BigDecimal.ZERO, BigDecimal::add)
        totalPriceTextView.text = "$${cost.setScale(2).toPlainString()}"
        dateTextView.text = DateFormat.getDateInstance(DateFormat.FULL).format(receipt.date)


        receipt.items.distinctBy { it.article.name }.forEach { receiptItem ->
            when (receiptItem.article.name) {
                "Fine-ground coffee" -> {
                    addView(receiptItem, receipt.items.count { it.article.name == "Fine-ground coffee"})
                }
                "Brownie" -> {
                    addView(receiptItem, receipt.items.count { it.article.name == "Brownie"})
                }
                "Croissant" -> {
                    addView(receiptItem, receipt.items.count { it.article.name == "Croissant"})
                }
                "Macaron" -> {
                    addView(receiptItem, receipt.items.count { it.article.name == "Macaron"})
                }
            }
        }
    }

    private fun addView(receiptItem: ReceiptItem, count: Int) {
        val linearLayout: LinearLayout = View.inflate(activity, R.layout.layout_purchase_item, null) as LinearLayout

        (linearLayout[0] as TextView).text = receiptItem.article.name
        (linearLayout[1] as TextView).text = count.toString()
        (linearLayout[2] as TextView).text = (receiptItem.calculateCost() * count.toBigDecimal()).setScale(2).stripTrailingZeros().toPlainString()

        itemsLinearLayout.addView(linearLayout)
    }

    private fun setFinishButtonOnClickListener() {
        paymentCompleteFinishButton.setOnClickListener {
            navigateToStart()
        }
    }

    private fun navigateToStart() {
        val action = PaymentReceiptFragmentDirections
            .actionNavigationPaymentReceiptToNavigationPaymentStart()
        findNavController().navigate(action)
    }

    override fun getBackPriority(): Int = BackFragment.NORMAL_BACK_PRIORITY

    override fun onBackPressed(): Boolean {
        navigateToStart()
        return true
    }
}
