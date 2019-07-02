package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.util.SETTINGS_UNLOCK
import com.radixdlt.android.apps.pos.util.SETTINGS_UPDATE
import com.radixdlt.android.apps.pos.util.SETTINGS_UPDATE_INVOICE_NAME
import com.radixdlt.android.apps.pos.util.SETTINGS_UPDATE_PAYMENT_ADDRESS
import com.radixdlt.android.apps.pos.util.SettingsPinAction
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.replaceWithAsterisk
import kotlinx.android.synthetic.main.fragment_settings_pin.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsPinFragment : Fragment() {

    private val args: SettingsPinFragmentArgs by navArgs()
    private val state: Int by lazy { args.state }
    private val paymentAddress: String by lazy { args.paymentAddress ?: "" }
    private val invoiceName: String by lazy { args.invoiceName ?: "" }

    private val pinSet: String = VaultPreferences.getVaultPIN()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_pin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnLayoutChangeListener(view)
        setOnKeypadTextChangeListener()
    }

    private fun setOnLayoutChangeListener(view: View) {
        view.doOnLayout {
            val params = settingsPinPinTextView.layoutParams as ViewGroup.LayoutParams
            params.width = settingsPinPinDisabledTextView.width
            settingsPinPinTextView.layoutParams = params
        }
    }

    private fun setOnKeypadTextChangeListener() {
        settingsPinKeypad.setOnTextChangeListener { pin: String, _: Int ->

            hideErrorMessageIfVisible()

            val hiddenPin = pin.replaceRange(0, pin.length, replaceWithAsterisk(pin))
            settingsPinPinTextView.text = hiddenPin

            if (pin.length == 4 && state == SETTINGS_UNLOCK) {
                validatePinMatchesMasterPin(pin, SETTINGS_UNLOCK)
            } else if (pin.length == 4 && state == SETTINGS_UPDATE && paymentAddress.isNotEmpty()) {
                validatePinMatchesMasterPin(pin, SETTINGS_UPDATE_PAYMENT_ADDRESS)
            } else if (pin.length == 4 && state == SETTINGS_UPDATE && invoiceName.isNotEmpty()) {
                validatePinMatchesMasterPin(pin, SETTINGS_UPDATE_INVOICE_NAME)
            }
        }
    }

    private fun hideErrorMessageIfVisible() {
        if (settingsPinPinErrorTextView.visibility == View.VISIBLE) {
            settingsPinPinErrorTextView.visibility = View.GONE
        }
    }

    private fun isValidPin(pin: String) = pin == pinSet

    private fun unlockSettings() {
        val action = SettingsPinFragmentDirections.actionNavigationSettingsPinToNavigationSettings()
        findNavController().navigate(action)
    }

    private fun validatePinMatchesMasterPin(pin: String, @SettingsPinAction action: Int) {
        if (isValidPin(pin)) {
            updateSettings(action)
        } else {
            displayIncorrectPinMessage()
        }
    }

    private fun updateSettings(@SettingsPinAction pinState: Int) {
        when (pinState) {
            SETTINGS_UNLOCK -> unlockSettings()
            SETTINGS_UPDATE_PAYMENT_ADDRESS -> updatePaymentAddress()
            SETTINGS_UPDATE_INVOICE_NAME -> updateInvoiceName()
        }
    }

    private fun updatePaymentAddress() {
        VaultPreferences.setVaultPaymentAddress(paymentAddress)
        navigateToSettingsUpdated()
    }

    private fun updateInvoiceName() {
        VaultPreferences.setVaultInvoiceName(invoiceName)
        navigateToSettingsUpdated()
    }

    private fun navigateToSettingsUpdated() {
        val action = SettingsPinFragmentDirections.actionNavigationSettingsPinToNavigationSettingsUpdated()
        findNavController().navigate(action)
    }

    private fun displayIncorrectPinMessage() {
        settingsPinPinErrorTextView.visibility = View.VISIBLE
        settingsPinPinErrorTextView.text = getString(R.string.settings_pin_fragment_incorrect_pin)
        clearPinWithDelay()
        settingsPinKeypad.clearDigits()
    }

    private fun clearPinWithDelay() {
        lifecycleScope.launch {
            delay(200)
            settingsPinPinTextView.text = ""
        }
    }
}
