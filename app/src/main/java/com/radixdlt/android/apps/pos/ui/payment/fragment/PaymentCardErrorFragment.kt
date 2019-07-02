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
import kotlinx.android.synthetic.main.fragment_payment_card_error.*
import net.skoumal.fragmentback.BackFragment

class PaymentCardErrorFragment : Fragment(), BackFragment {

    private val args: PaymentCardErrorFragmentArgs by navArgs()
    private val amount: String by lazy { args.amount ?: ZERO }
    private val reference: String by lazy { args.reference ?: "" }
    private val cardBlocked: Boolean by lazy { args.cardBlocked }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_card_error, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayErrorMessage()
        playCardErrorAnimation()
        setOnClickListeners()
    }

    private fun playCardErrorAnimation() {
        paymentCardErrorAnimationView.playAnimation()
    }

    private fun displayErrorMessage() {
        if (cardBlocked) {
            paymentCardErrorErrorTextView.text = getString(R.string.fragment_payment_card_error_card_blocked)
        } else {
            paymentCardErrorErrorTextView.text = getString(R.string.fragment_payment_card_error_insufficient_funds)
        }
    }

    private fun setOnClickListeners() {
        paymentCardErrorCancelTransactionButton.setOnClickListener {
            navigateToTransactionCancelled()
        }

        paymentCardErrorUseAnotherCardButton.setOnClickListener {
            val action = PaymentCardErrorFragmentDirections
                .actionNavigationPaymentCardErrorToNavigationPaymentTapScan(amount, reference)
            findNavController().navigate(action)
        }
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        navigateToTransactionCancelled()
        return true
    }

    private fun navigateToTransactionCancelled() {
        findNavController().navigate(
            R.id.action_navigation_payment_card_error_to_navigation_payment_cancelled
        )
    }
}
