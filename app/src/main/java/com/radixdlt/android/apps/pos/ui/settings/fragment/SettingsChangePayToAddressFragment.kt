package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.ui.setup.BarcodeCaptureActivity
import com.radixdlt.android.apps.pos.util.SETTINGS_UPDATE
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.hideKeyboard
import com.radixdlt.android.apps.pos.util.isRadixAddress
import com.radixdlt.android.apps.pos.util.setButtonDrawableOnTopOfText
import com.radixdlt.android.apps.pos.util.toast
import kotlinx.android.synthetic.main.fragment_settings_change_pay_to_address.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.skoumal.fragmentback.BackFragment
import timber.log.Timber

class SettingsChangePayToAddressFragment : BaseFragment(), BackFragment {

    private val savedPaymentAddress: String = VaultPreferences.getVaultPaymentAddress()
    private var newAddress: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_change_pay_to_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseToolBar()
        initialiseInfoToDisplay()
        setButtonDrawablesOnTopOfText()
        setListeners()
    }

    private fun initialiseInfoToDisplay() {
        settingsChangePayToAddressTypeAddressContinueButton.backgroundTintList =
            setButtonColor(R.color.disabledLightGray)
        settingsChangePayToAddressTypeAddressContinueButton.isEnabled = false
        settingsChangePayToAddressDescriptionTextView.text =
            getString(R.string.settings_change_pay_to_address_current_address)
        settingsChangePayToAddressEditText.setText(savedPaymentAddress)
    }

    private fun setButtonDrawablesOnTopOfText() {
        pasteAddressButton.setButtonDrawableOnTopOfText()
        scanAddressButton.setButtonDrawableOnTopOfText()
    }

    private fun setListeners() {
        setButtonOnClickListeners()
        setPaymentsAddressEditTextListeners()
    }

    private fun setPaymentsAddressEditTextListeners() {
        settingsChangePayToAddressEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                settingsChangePayToAddressContinueButton.visibility = View.GONE
                settingsChangePayToAddressNoKeyboardGroup.visibility = View.GONE
                settingsChangePayToAddressKeyboardVisibleGroup.visibility = View.VISIBLE
                lifecycleScope.launch {
                    delay(200)
                    settingsChangePayToAddressScrollView.smoothScrollTo(0, settingsChangePayToAddressScrollView.bottom)
                }
            } else if (isRadixAddress(newAddress) && newAddress != savedPaymentAddress) {
                settingsChangePayToAddressContinueButton.visibility = View.VISIBLE
                settingsChangePayToAddressNoKeyboardGroup.visibility = View.VISIBLE
                settingsChangePayToAddressKeyboardVisibleGroup.visibility = View.GONE
            } else {
                settingsChangePayToAddressNoKeyboardGroup.visibility = View.VISIBLE
                settingsChangePayToAddressKeyboardVisibleGroup.visibility = View.GONE
            }
        }

        settingsChangePayToAddressEditText.setBackEventListener { editText, _ ->
            lifecycleScope.launch {
                delay(200)
                editText.clearFocus()
            }
        }

        settingsChangePayToAddressEditText.doAfterTextChanged {
            newAddress = it.toString()
            setContinueButtonState()
        }
    }

    private fun setButtonOnClickListeners() {
        settingsChangePayToAddressContinueButton.setOnClickListener {
            val action = SettingsChangePayToAddressFragmentDirections
                .actionNavigationSettingsChangePayToAddressToNavigationSettingsConfirmMasterPin(
                    SETTINGS_UPDATE,
                    settingsChangePayToAddressEditText.text.toString()
                )
            findNavController().navigate(action)
        }

        settingsChangePayToAddressTypeAddressContinueButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_settings_change_pay_to_address_to_navigation_settings_confirm_master_pin
            )
        }

        scanAddressButton.setOnClickListener {
            startActivityForResult(
                Intent(activity, BarcodeCaptureActivity::class.java),
                RC_BARCODE_CAPTURE
            )
        }

        pasteAddressButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip?.getItemAt(0)?.let {
                val radixAddressToPaste = it.text.toString()
                settingsChangePayToAddressEditText.setText(radixAddressToPaste)
                if (isRadixAddress(radixAddressToPaste) && savedPaymentAddress != newAddress) {
                    settingsChangePayToAddressContinueButton.visibility = View.VISIBLE
                }
            }
        }

        backButton.setOnClickListener {
            hideKeyboard()
            lifecycleScope.launch {
                delay(200)
                settingsChangePayToAddressEditText.clearFocus()
            }
        }
    }

    private fun setContinueButtonState() {
        if (isRadixAddress(newAddress) && newAddress != savedPaymentAddress) {
            settingsChangePayToAddressTypeAddressContinueButton.backgroundTintList =
                setButtonColor(R.color.radixGreen)
            settingsChangePayToAddressTypeAddressContinueButton.isEnabled = true
            settingsChangePayToAddressDescriptionTextView.text =
                getString(R.string.settings_change_pay_to_address_new_address)
        } else if (isRadixAddress(newAddress) && newAddress == savedPaymentAddress) {
            settingsChangePayToAddressTypeAddressContinueButton.backgroundTintList =
                setButtonColor(R.color.disabledLightGray)
            settingsChangePayToAddressTypeAddressContinueButton.isEnabled = false
            settingsChangePayToAddressDescriptionTextView.text =
                getString(R.string.settings_change_pay_to_address_current_address)
        } else {
            settingsChangePayToAddressTypeAddressContinueButton.backgroundTintList =
                setButtonColor(R.color.disabledLightGray)
            settingsChangePayToAddressTypeAddressContinueButton.isEnabled = false
            settingsChangePayToAddressDescriptionTextView.text =
                getString(R.string.settings_change_pay_to_address_enter_address)
        }
    }

    override fun onResume() {
        super.onResume()
        if (newAddress.isNotEmpty() && isRadixAddress(newAddress) && newAddress != savedPaymentAddress) {
            settingsChangePayToAddressContinueButton.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode: Barcode = data.getParcelableExtra(BarcodeCaptureActivity.BARCODE_OBJECT)
                    val address = barcode.displayValue.trim()
                    newAddress = address
                    settingsChangePayToAddressEditText.setText(address)
                    settingsChangePayToAddressEditText.clearFocus()
                    Timber.d("Barcode read: $address")
                    settingsChangePayToAddressContinueButton.visibility = View.VISIBLE
                } else {
                    toast(getString(R.string.barcode_failure))
                    Timber.d("No barcode captured, intent data is null")
                }
            } else {
                val failureString = getString(R.string.barcode_error, CommonStatusCodes.getStatusCodeString(resultCode))
                toast(getString(R.string.toast_error_reading_qr_code))
                Timber.e(failureString)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun initialiseToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(
            R.id.action_navigation_settings_change_pay_to_address_to_navigation_settings
        )
        return true
    }

    companion object {
        private const val RC_BARCODE_CAPTURE = 9001
    }
}
