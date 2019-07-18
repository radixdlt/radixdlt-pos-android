package com.radixdlt.android.apps.pos.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.ui.payment.ConnectionStateMonitor
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentRadixNetworkState
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentViewModel
import com.radixdlt.android.apps.pos.ui.settings.SettingsActivity
import com.radixdlt.android.apps.pos.util.startActivity
import kotlinx.android.synthetic.main.fragment_payment_start.*
import timber.log.Timber

class PaymentStartFragment : BaseFragment() {

    private val paymentViewModel: PaymentViewModel by activityViewModels()
    private var paymentRadixNetworkState = PaymentRadixNetworkState.DISCONNECTED
    private var isInternetConnected = false
    private var allConnected = false

    private val connectionStateMonitor = object : ConnectionStateMonitor() {
        override fun isConnected(connected: Boolean) {
            Timber.tag("CONNECTED").d("connectionStateMonitor: $connected")
            isInternetConnected = connected
            allConnected = (paymentRadixNetworkState == PaymentRadixNetworkState.CONNECTED) && isInternetConnected
            setStartButtonState(allConnected, paymentRadixNetworkState)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setBusinessName()
        setStartButtonOnClickListener()
        initialiseViewModel()
    }

    private fun initialiseViewModel() {
        paymentViewModel.radixNetworkConnection.observe(::getLifecycle) {
            paymentRadixNetworkState = it
            allConnected = (paymentRadixNetworkState == PaymentRadixNetworkState.CONNECTED) && isInternetConnected
            setStartButtonState(allConnected, it)
        }
    }

    private fun setBusinessName() {
        paymentStartBusinessNameTextView.text = getString(R.string.fragment_payment_start_business_name)
    }

    private fun setStartButtonOnClickListener() {
        paymentStartStartButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_payment_start_to_navigation_payment_input_amount
            )
        }
    }

    private fun setStartButtonState(isConnected: Boolean, status: PaymentRadixNetworkState) {
        paymentStartStartButton.isEnabled = isConnected

        when {
            isConnected -> {
                paymentStartStartButton.backgroundTintList = setButtonColor(R.color.radixGreen)
                paymentStartNoNetworkStatusTextView.visibility = View.GONE
            }
            status == PaymentRadixNetworkState.CONNECTING -> {
                paymentStartStartButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
                paymentStartNoNetworkStatusTextView.setText(R.string.fragment_payment_start_connecting)
                paymentStartNoNetworkStatusTextView.visibility = View.VISIBLE
                paymentStartNoNetworkStatusTextView.setTextColor(ContextCompat.getColor(ctx, R.color.mustardYellow))
            }
            else -> {
                paymentStartStartButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
                paymentStartNoNetworkStatusTextView.setText(R.string.fragment_payment_start_no_connection)
                paymentStartNoNetworkStatusTextView.setTextColor(ContextCompat.getColor(ctx, R.color.lightRed))
                paymentStartNoNetworkStatusTextView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_payment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        connectionStateMonitor.enable(ctx)
    }

    override fun onPause() {
        connectionStateMonitor.disable(ctx)
        super.onPause()
    }
}
