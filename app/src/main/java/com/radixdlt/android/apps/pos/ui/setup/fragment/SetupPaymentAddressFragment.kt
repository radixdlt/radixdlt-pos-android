package com.radixdlt.android.apps.pos.ui.setup.fragment

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.ui.setup.BarcodeCaptureActivity
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.hideKeyboard
import com.radixdlt.android.apps.pos.util.isRadixAddress
import com.radixdlt.android.apps.pos.util.setButtonDrawableOnTopOfText
import com.radixdlt.android.apps.pos.util.toast
import kotlinx.android.synthetic.main.fragment_setup_payment.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class SetupPaymentAddressFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setup_payment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setButtonDrawablesOnTopOfText()
        setContinueButtonState()
    }

    private fun setListeners() {
        setButtonOnClickListeners()
        setPaymentAddressEditTextListeners()
    }

    private fun setPaymentAddressEditTextListeners() {
        setupPaymentAddressEditText.setOnFocusChangeListener { _, hasFocus ->
            when {
                hasFocus -> {
                    setupPaymentAddressContinueButton.visibility = View.GONE
                    setupPaymentNoKeyboardGroup.visibility = View.GONE
                    setupPaymentKeyboardVisibleGroup.visibility = View.VISIBLE
                    lifecycleScope.launch {
                        delay(300)
                        setupPaymentScrollView.smoothScrollTo(0, setupPaymentScrollView.bottom)
                    }
                }
                else -> {
                    setupPaymentKeyboardVisibleGroup.visibility = View.GONE
                    setupPaymentNoKeyboardGroup.visibility = View.VISIBLE
                    hideKeyboard()
                }
            }
        }

        setupPaymentAddressEditText.setBackEventListener { editText, _ ->
            lifecycleScope.launch {
                delay(200)
                editText.clearFocus()
                if (isRadixAddress(setupPaymentAddressEditText.text.toString())) {
                    setupPaymentAddressContinueButton.visibility = View.VISIBLE
                }
            }
        }

        setupPaymentAddressEditText.doAfterTextChanged {
            setContinueButtonState()
        }
    }

    private fun setButtonOnClickListeners() {
        setupPaymentAddressContinueButton.setOnClickListener {
            VaultPreferences.setVaultPaymentAddress(setupPaymentAddressEditText.text.toString())
            findNavController().navigate(
                R.id.action_navigation_setup_payment_address_to_navigation_setup_business
            )
        }

        setupPaymentAddressTypeAddressContinueButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_setup_payment_address_to_navigation_setup_business
            )
        }

        setupPaymentAddressScanAddressButton.setOnClickListener {
            startActivityForResult(
                Intent(activity, BarcodeCaptureActivity::class.java),
                RC_BARCODE_CAPTURE
            )
        }

        setupPaymentAddressPasteAddressButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.primaryClip?.getItemAt(0)?.let {
                val radixAddressToPaste = it.text.toString()
                setupPaymentAddressEditText.setText(radixAddressToPaste)
                if (isRadixAddress(radixAddressToPaste)) {
                    setupPaymentAddressContinueButton.visibility = View.VISIBLE
                }
            }
        }

        setupPaymentAddressBackButton.setOnClickListener {
            hideKeyboard()
            lifecycleScope.launch {
                delay(200)
                setupPaymentAddressEditText.clearFocus()
                setContinueButtonState()
                if (isRadixAddress(setupPaymentAddressEditText.text.toString())) {
                    setupPaymentAddressContinueButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setButtonDrawablesOnTopOfText() {
        setupPaymentAddressPasteAddressButton.setButtonDrawableOnTopOfText()
        setupPaymentAddressScanAddressButton.setButtonDrawableOnTopOfText()
    }

    private fun setContinueButtonState() {
        if (isRadixAddress(setupPaymentAddressEditText.text.toString())) {
            setupPaymentAddressTypeAddressContinueButton.backgroundTintList = setButtonColor(R.color.radixGreen)
            setupPaymentAddressTypeAddressContinueButton.isEnabled = true
        } else {
            setupPaymentAddressTypeAddressContinueButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            setupPaymentAddressTypeAddressContinueButton.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        val setupPaymentAddress = setupPaymentAddressEditText.text.toString()
        if (setupPaymentAddress.isNotEmpty() && isRadixAddress(setupPaymentAddress)) {
            setupPaymentAddressContinueButton.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        setupPaymentAddressEditText.clearFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    val barcode: Barcode = data.getParcelableExtra(BarcodeCaptureActivity.BARCODE_OBJECT)
                    val address = barcode.displayValue.trim()
                    if (isRadixAddress(address)) {
                        setupPaymentAddressEditText.setText(address)
                        setupPaymentAddressEditText.clearFocus()
                        setupPaymentAddressContinueButton.visibility = View.VISIBLE
                    } else {
                        toast(getString(R.string.setup_payment_address_fragment_not_a_radix_address_toast))
                    }
                } else {
                    toast(getString(R.string.barcode_failure))
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

    companion object {
        private const val RC_BARCODE_CAPTURE = 9001
    }
}
