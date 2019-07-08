package com.radixdlt.android.apps.pos.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.util.ZERO
import com.radixdlt.android.apps.pos.util.formatUSADollar
import kotlinx.android.synthetic.main.fragment_payment_complete.*
import net.skoumal.fragmentback.BackFragment

class PaymentCompleteFragment : Fragment(), BackFragment {

    private val args: PaymentCompleteFragmentArgs by navArgs()
    private val amountText: String by lazy { args.amount ?: ZERO }
    private val reference: String by lazy { args.reference ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_complete, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayAmountPayed()
        playPaymentSuccessAnimation()
        setShowReceiptOnClickListener()
        setFinishButtonOnClickListener()
    }

    private fun displayAmountPayed() {
        paymentCompleteAmountPayedTextView.text = formatUSADollar(amountText.toBigDecimal())
    }

    private fun playPaymentSuccessAnimation() {
        paymentCompleteSuccessAnimationView.visibility = View.VISIBLE
        paymentCompleteSuccessAnimationView.playAnimation()
    }

    private fun setShowReceiptOnClickListener() {
        paymentCompleteReceiptButton.setOnClickListener {
            val action = PaymentCompleteFragmentDirections
                .actionNavigationPaymentCompleteToNavigationPaymentReceipt(reference)
            findNavController().navigate(action)
        }
    }

    private fun setFinishButtonOnClickListener() {
        paymentCompleteFinishButton.setOnClickListener {
            navigateToStart()
        }
    }

    private fun navigateToStart() {
        val action = PaymentCompleteFragmentDirections.actionNavigationPaymentCompleteToNavigationPaymentStart()
        findNavController().navigate(action)
    }

    override fun getBackPriority(): Int = BackFragment.NORMAL_BACK_PRIORITY

    override fun onBackPressed(): Boolean {
        navigateToStart()
        return true
    }
}
