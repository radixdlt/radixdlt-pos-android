package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.settings.dialog.ResetDataProgressDialog
import com.radixdlt.android.apps.pos.util.INVOICE_NAME
import com.radixdlt.android.apps.pos.util.MASTER_PIN
import com.radixdlt.android.apps.pos.util.PAYMENT_ADDRESS
import com.radixdlt.android.apps.pos.util.Vault
import com.radixdlt.android.apps.pos.util.VaultPreferences
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.skoumal.fragmentback.BackFragment

class SettingsFragment : Fragment(), BackFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAddressAndInvoiceName()
        setOnclickListeners()
    }

    private fun setAddressAndInvoiceName() {
        settingsPaymentAddressTextView.text = VaultPreferences.getVaultPaymentAddress()
        settingsInvoiceNameTextView.text = VaultPreferences.getVaultInvoiceName()
    }

    private fun setOnclickListeners() {
        settingsPaymentAddressLayout.setOnClickListener {
            val action = SettingsFragmentDirections
                .actionNavigationSettingsToNavigationSettingsUserUnderstanding(PAYMENT_ADDRESS)
            findNavController().navigate(action)
        }

        settingsInvoiceNameLayout.setOnClickListener {
            val action = SettingsFragmentDirections
                .actionNavigationSettingsToNavigationSettingsUserUnderstanding(INVOICE_NAME)
            findNavController().navigate(action)
        }

        settingsMasterPinLayout.setOnClickListener {
            val action = SettingsFragmentDirections
                .actionNavigationSettingsToNavigationSettingsUserUnderstanding(MASTER_PIN)
            findNavController().navigate(action)
        }

        settingsObtainAddressCardView.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_settings_to_navigation_settings_settings_obtain_address
            )
        }

        settingsResetAppCardView.setOnClickListener {
            lifecycleScope.launch {
                val resetDataProgressDialog = ResetDataProgressDialog()
                resetDataProgressDialog.isCancelable = false
                resetDataProgressDialog.show(requireFragmentManager(), ResetDataProgressDialog.TAG)
                withContext(Dispatchers.IO) { Vault.resetKey() }
                resetDataProgressDialog.cancel()
                activity?.finishAffinity()
            }
        }
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        activity?.finish()
        return true
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}
