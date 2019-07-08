package com.radixdlt.android.apps.pos.ui.payment.pin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentViewModel
import com.radixdlt.android.apps.pos.util.replaceWithAsterisk
import kotlinx.android.synthetic.main.fragment_payment_pin.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentPinFragment : BaseFragment() {

    private val args: PaymentPinFragmentArgs by navArgs()
    private val amount: String by lazy { args.amount ?: "0.00" }
    private val reference: String by lazy { args.reference ?: "" }
    private val publicKey: String by lazy { args.publicKey ?: "" }
    private var pinSet: String = ""

    private var isFinishedBuildingAtom = false
    private var isInsufficientFunds = false

    private val paymentViewModel: PaymentViewModel by activityViewModels()
    private val paymentPinViewModel: PaymentPinViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_pin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialiseViewModel()
        setListeners(view)
    }

    private fun setListeners(view: View) {
        setOnLayoutChangeListener(view)
        setConfirmButtonOnClickListener()
        setKeypadOnTextChangeListener()
    }

    private fun initialiseViewModel() {
        isFinishedBuildingAtom = false
        paymentPinViewModel.paymentPinAtomBuildingState.observe(::getLifecycle, ::atomBuildingState)
        lifecycleScope.launch {
            delay(50)
//            paymentPinViewModel.buildAtom(publicKey, amount, reference)
            paymentPinViewModel.buildAtom2(publicKey, paymentViewModel.purchase)
//            paymentPinViewModel.buildAtomForReceipt(publicKey, paymentViewModel.purchase)
        }
    }

    private fun setConfirmButtonOnClickListener() {
        paymentPinConfirmButton.setOnClickListener {
            if (isInsufficientFunds) {
                val action = PaymentPinFragmentDirections
                    .actionNavigationPaymentPinToNavigationPaymentCardError(paymentViewModel.purchase.costOfArticles().toString(), reference = reference)
                findNavController().navigate(action)
                return@setOnClickListener
            }
            val action = PaymentPinFragmentDirections
                .actionNavigationPaymentPinToNavigationPaymentTapToPay(paymentViewModel.purchase.costOfArticles().toString(), reference, pinSet, publicKey)
            findNavController().navigate(action)
            pinSet = ""
        }
    }

    private fun setKeypadOnTextChangeListener() {
        paymentPinKeypad.setOnTextChangeListener { pin: String, digitsRemaining: Int ->
            if (paymentViewModel.incorrectPin.isNotEmpty()) {
                paymentPinErrorTextView.visibility = View.GONE
                paymentPinErrorTextView.text = ""
                paymentViewModel.incorrectPin = ""
            }

            if (digitsRemaining == 0 && isFinishedBuildingAtom) {
                paymentPinConfirmButton.backgroundTintList = setButtonColor(R.color.radixGreen)
                paymentPinConfirmButton.isEnabled = true
            } else {
                paymentPinConfirmButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
                paymentPinConfirmButton.isEnabled = false
            }

            pinSet = pin

            val hiddenPin = pin.replaceRange(0, pin.length, replaceWithAsterisk(pin))
            paymentPinPinTextView.text = hiddenPin
        }
    }

    private fun setOnLayoutChangeListener(view: View) {
        view.doOnLayout {
            val params = paymentPinPinTextView.layoutParams as ViewGroup.LayoutParams
            params.width = paymentPinPinDisabledTextView.width
            paymentPinPinTextView.layoutParams = params
        }
    }

    private fun atomBuildingState(paymentPinAtomBuildingState: PaymentPinAtomBuildingState) {
        when (paymentPinAtomBuildingState) {
            is PaymentPinAtomBuildingState.UnsignedAtomBuilt -> {
                paymentViewModel.unsignedAtom = paymentPinAtomBuildingState.unsignedAtom
                activateConfirmButton()
            }
            PaymentPinAtomBuildingState.InsufficientFunds -> {
                isInsufficientFunds = true
                activateConfirmButton()
            }
            PaymentPinAtomBuildingState.Error -> activateConfirmButton()
        }
    }

    override fun onResume() {
        super.onResume()
        checkForIncorrectPin()
    }

    private fun checkForIncorrectPin() {
        if (paymentViewModel.incorrectPin.isNotEmpty()) {
            paymentPinErrorTextView.visibility = View.VISIBLE
            paymentPinErrorTextView.text = paymentViewModel.incorrectPin
        }
    }

    private fun activateConfirmButton() {
        isFinishedBuildingAtom = true
        paymentPinConfirmButton.text = getString(R.string.fragment_payment_pin_confirm)
        if (pinSet.length == 4) {
            paymentPinConfirmButton.backgroundTintList = setButtonColor(R.color.radixGreen)
            paymentPinConfirmButton.isEnabled = true
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        paymentPinViewModel.clearDisposable()
    }
}
