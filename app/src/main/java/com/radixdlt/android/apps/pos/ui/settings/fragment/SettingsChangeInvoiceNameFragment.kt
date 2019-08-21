package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.SETTINGS_UPDATE
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.hideKeyboard
import com.radixdlt.android.apps.pos.util.isKeyboardOpen
import kotlinx.android.synthetic.main.fragment_settings_change_invoice_name.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.skoumal.fragmentback.BackFragment

class SettingsChangeInvoiceNameFragment : BaseFragment(), BackFragment {

    private val savedInvoiceName: String = VaultPreferences.getVaultInvoiceName()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_change_invoice_name, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseToolBar()
        setCurrentInvoiceName()
        setListeners()
    }

    private fun setListeners() {
        setOnClickListeners()
        setBusinessNameEditTextListeners()
    }

    private fun setBusinessNameEditTextListeners() {
        settingsChangeInvoiceNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                lifecycleScope.launch {
                    delay(200)
                    settingsChangeInvoiceNameScrollView.smoothScrollTo(0, settingsChangeInvoiceNameScrollView.bottom)
                }
            }
        }

        settingsChangeInvoiceNameEditText.setBackEventListener { editText, _ ->
            editText.isEnabled = false
            lifecycleScope.launch {
                delay(200)
                editText.clearFocus()
                editText.isEnabled = true
            }
        }

        settingsChangeInvoiceNameEditText.doAfterTextChanged {
            // if it is a radix address change continue to green else keep gray
            setContinueButtonState()
        }
    }

    private fun setOnClickListeners() {
        settingsChangeInvoiceNameContinueButton.setOnClickListener {
            hideKeyboard()
            lifecycleScope.launch {
                delay(200)
                val action = SettingsChangeInvoiceNameFragmentDirections
                    .actionNavigationSettingsChangeInvoiceNameToNavigationSettingsConfirmMasterPin(
                        SETTINGS_UPDATE,
                        settingsChangeInvoiceNameEditText.text.toString()
                    )
                findNavController().navigate(action)
            }
        }

        settingsChangeInvoiceNameBackButton.setOnClickListener {
            if (isKeyboardOpen()) {
                hideKeyboard()
                settingsChangeInvoiceNameEditText.clearFocus()
            } else {
                findNavController().navigate(
                    R.id.action_navigation_settings_change_invoice_name_to_address_to_navigation_settings
                )
            }
        }
    }

    private fun setCurrentInvoiceName() {
        settingsChangeInvoiceNameEditText.setText(savedInvoiceName)
    }

    private fun setContinueButtonState() {

        val businessName = settingsChangeInvoiceNameEditText.text?.toString() ?: ""

        if (businessName.isNotEmpty() && businessName != savedInvoiceName) {
            settingsChangeInvoiceNameContinueButton.backgroundTintList = setButtonColor(R.color.radixGreen)
            settingsChangeInvoiceNameContinueButton.isEnabled = true
            settingsChangeInvoiceNameDescriptionTextView.text =
                getString(R.string.settings_change_invoice_name_fragment_new_name_to_be)
        } else if (businessName.isNotEmpty() && savedInvoiceName == businessName) {
            settingsChangeInvoiceNameDescriptionTextView.text =
                getString(R.string.settings_change_invoice_name_fragment_current_name)
            settingsChangeInvoiceNameContinueButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            settingsChangeInvoiceNameContinueButton.isEnabled = false
        } else {
            settingsChangeInvoiceNameContinueButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            settingsChangeInvoiceNameContinueButton.isEnabled = false
            settingsChangeInvoiceNameDescriptionTextView.text =
                getString(R.string.settings_change_invoice_name_fragment_enter_new_name)
        }
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(
            R.id.action_navigation_settings_change_invoice_name_to_address_to_navigation_settings
        )
        return true
    }

    private fun initialiseToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }
}
