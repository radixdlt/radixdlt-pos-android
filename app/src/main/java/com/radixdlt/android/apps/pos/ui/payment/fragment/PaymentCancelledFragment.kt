package com.radixdlt.android.apps.pos.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import kotlinx.android.synthetic.main.fragment_payment_cancelled.*
import net.skoumal.fragmentback.BackFragment

class PaymentCancelledFragment : Fragment(), BackFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_cancelled, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playPaymentCancelledAnimation()
        setPaymentCancelledOnClickListener()
    }

    private fun setPaymentCancelledOnClickListener() {
        paymentCancelledFinishButton.setOnClickListener {
            navigateToStart()
        }
    }

    private fun playPaymentCancelledAnimation() {
        paymentCancelledAnimationView.playAnimation()
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        navigateToStart()
        return true
    }

    private fun navigateToStart() {
        findNavController().navigate(
            R.id.action_navigation_payment_cancelled_to_navigation_payment_start
        )
    }
}
