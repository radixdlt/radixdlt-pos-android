package com.radixdlt.android.apps.pos.ui.payment.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.ZERO
import com.radixdlt.android.apps.pos.util.formatUSADollar
import kotlinx.android.synthetic.main.fragment_payment_input_amount.*
import java.math.BigDecimal

class PaymentInputAmountFragment : BaseFragment() {

    private var chargeAmount = ZERO
    private var addedChargeAmount = BigDecimal.ZERO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_payment_input_amount, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialiseViews()
    }

    private fun initialiseViews() {
        if (paymentInputAmountTextView.text.isEmpty()) showFormattedAmount(ZERO)
        setListeners()
    }

    private fun showFormattedAmount(amount: String) {
        paymentInputAmountTextView.text = formatUSADollar(BigDecimal(amount))
    }

    private fun setListeners() {
        setOnClickListeners()
        setReferenceEditTextBackEventListener()
        setKeyPadOnTextChangeListener()
    }

    private fun setKeyPadOnTextChangeListener() {
        paymentInputAmountKeypad.setOnTextChangeListener { amount: String, _: Int ->
            if (amount.isEmpty()) {
                showFormattedAmount(ZERO)
                chargeAmount = ZERO
                setChargeButtonState(chargeAmount)
            } else {
                val amountDecimal = decimalSymbolCheckAndReplace(amount)
                chargeAmount = amountDecimal
                showFormattedAmount(amountDecimal)
                setChargeButtonState(amountDecimal)
            }
        }
    }

    private fun decimalSymbolCheckAndReplace(amount: String) =
        if (amount.contains(",")) amount.replace(",", ".") else amount

    private fun setReferenceEditTextBackEventListener() {
        paymentInputAmountReferenceEditText.setBackEventListener { backPressedEditText, _ ->
            backPressedEditText.clearFocus()
        }
    }

    private fun setOnClickListeners() {
        paymentInputAmountChargeButton.setOnClickListener {
            val reference = if (paymentInputAmountReferenceEditText.text.isNullOrEmpty()) {
                getString(R.string.fragment_payment_input_amount_reference, VaultPreferences.getVaultInvoiceName())
            } else {
                getString(
                    R.string.fragment_payment_input_amount_reference_description,
                    VaultPreferences.getVaultInvoiceName(), paymentInputAmountReferenceEditText.text.toString()
                )
            }
            val action =
                PaymentInputAmountFragmentDirections.actionNavigationPaymentInputAmountToNavigationPaymentTapScan(
                    if (addedChargeAmount != BigDecimal.ZERO) addedChargeAmount.toString() else chargeAmount,
                    reference
                )
            findNavController().navigate(action)
        }

        paymentInputAmountAddButton.setOnClickListener {
            if (chargeAmount == ZERO) return@setOnClickListener
            totalGroup.visibility = View.VISIBLE
            addedChargeAmount += chargeAmount.toBigDecimal()
            paymentInputAmountKeypad.clearDigits()

            chargeAmount = ZERO
            paymentInputAmountTotalTextView.text = formatUSADollar(addedChargeAmount)
            paymentInputAmountTextView.text = formatUSADollar(BigDecimal.ZERO)
        }

        paymentInputAmountClearTotalButton.setOnClickListener {
            totalGroup.visibility = View.GONE
            addedChargeAmount = BigDecimal.ZERO
            chargeAmount = ZERO
            setChargeButtonState(chargeAmount)
            paymentInputAmountTextView.text = formatUSADollar(chargeAmount.toBigDecimal())
        }
    }

    private fun setChargeButtonState(amount: String) {
        if (amount != ZERO || addedChargeAmount != BigDecimal.ZERO) {
            paymentInputAmountChargeButton.backgroundTintList = setButtonColor(R.color.radixGreen)
            paymentInputAmountChargeButton.isEnabled = true
        } else {
            paymentInputAmountChargeButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            paymentInputAmountChargeButton.isEnabled = false
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
}
