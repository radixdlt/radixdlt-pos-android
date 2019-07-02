package com.radixdlt.android.apps.pos.ui.setup.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.hideKeyboard
import com.radixdlt.android.apps.pos.util.isKeyboardOpen
import kotlinx.android.synthetic.main.fragment_setup_invoice_name.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SetupInvoiceNameFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setup_invoice_name, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonOnClickListeners()
        setBusinessNameEditTextListeners()
        setContinueButtonState()
    }

    private fun setBusinessNameEditTextListeners() {
        setupInvoiceBusinessNameEditText.setBackEventListener { editText, _ ->
            editText.clearFocus()
        }

        setupInvoiceBusinessNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lifecycleScope.launch {
                    delay(300)
                    setupInvoiceNameScrollView.smoothScrollTo(0, setupInvoiceNameScrollView.bottom)
                }
            }
        }

        setupInvoiceBusinessNameEditText.doAfterTextChanged {
            setContinueButtonState()
        }
    }

    private fun setButtonOnClickListeners() {
        setupInvoiceTypeAddressContinueButton.setOnClickListener {
            VaultPreferences.setVaultInvoiceName(setupInvoiceBusinessNameEditText.text.toString())
            hideKeyboard()
            lifecycleScope.launch {
                delay(200)
                findNavController().navigate(
                    R.id.action_navigation_setup_business_to_navigation_setup_complete
                )
            }
        }

        setupInvoiceBackButton.setOnClickListener {
            if (isKeyboardOpen()) {
                hideKeyboard()
                setupInvoiceBusinessNameEditText.clearFocus()
            } else {
                activity?.onBackPressed()
            }
        }
    }

    private fun setContinueButtonState() {
        if (setupInvoiceBusinessNameEditText.text.isNullOrEmpty()) {
            setupInvoiceTypeAddressContinueButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            setupInvoiceTypeAddressContinueButton.isEnabled = false
        } else {
            setupInvoiceTypeAddressContinueButton.backgroundTintList = setButtonColor(R.color.radixGreen)
            setupInvoiceTypeAddressContinueButton.isEnabled = true
        }
    }
}
