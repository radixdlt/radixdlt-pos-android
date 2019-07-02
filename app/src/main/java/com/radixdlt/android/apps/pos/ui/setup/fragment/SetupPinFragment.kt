package com.radixdlt.android.apps.pos.ui.setup.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.replaceWithAsterisk
import kotlinx.android.synthetic.main.fragment_setup_pin.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupPinFragment : BaseFragment() {

    private lateinit var pinSet: String
    private var setupPinState: SetupPinState = SetupPinState.SET

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setup_pin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPinHeaderText()
        setOnLayoutChangeListener(view)
        setOnKeypadTextChangeListener()
    }

    private fun setPinHeaderText() {
        if (setupPinState == SetupPinState.SET) {
            setupTitleTextView.text = getText(R.string.setup_pin_fragment_set_pin_header)
        } else {
            setupTitleTextView.text = getText(R.string.setup_pin_fragment_confirm_pin_header)
        }
    }

    private fun setOnLayoutChangeListener(view: View) {
        view.doOnLayout {
            val params = setupPinTextView.layoutParams as ViewGroup.LayoutParams
            params.width = setupPinDisabledTextView.width
            setupPinTextView.layoutParams = params
        }
    }

    private fun setOnKeypadTextChangeListener() {
        setupPinKeypad.setOnTextChangeListener { pin: String, _: Int ->

            if (setupPinErrorTextView.visibility == View.VISIBLE) {
                setupPinErrorTextView.visibility = View.GONE
            }

            val hiddenPin = pin.replaceRange(0, pin.length, replaceWithAsterisk(pin))
            setupPinTextView.text = hiddenPin

            if (pin.length == 4 && setupPinState == SetupPinState.SET) {
                setupTitleTextView.text = getText(R.string.setup_pin_fragment_confirm_pin_header)
                clearHiddenPinWithDelay()
                pinSet = pin
                setupPinKeypad.clearDigits()
                setupPinState = SetupPinState.CONFIRM
            } else if (pin.length == 4 && setupPinState == SetupPinState.CONFIRM) {
                if (pin == pinSet) {
                    VaultPreferences.setVaultPIN(pin)
                    findNavController().navigate(
                        R.id.action_navigation_setup_pin_to_navigation_setup_payment_address
                    )
                } else {
                    setupPinErrorTextView.visibility = View.VISIBLE
                    setupPinErrorTextView.text = getText(R.string.setup_pin_fragment_pin_does_not_match)
                    clearHiddenPinWithDelay()
                    setupPinKeypad.clearDigits()
                }
            }
        }
    }

    private fun clearHiddenPinWithDelay() {
        lifecycleScope.launch {
            delay(200)
            setupPinTextView.text = ""
        }
    }

    private enum class SetupPinState {
        SET, CONFIRM
    }
}
