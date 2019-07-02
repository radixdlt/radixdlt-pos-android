package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.INVOICE_NAME
import com.radixdlt.android.apps.pos.util.MASTER_PIN
import com.radixdlt.android.apps.pos.util.PAYMENT_ADDRESS
import com.radixdlt.android.apps.pos.util.Settings
import kotlinx.android.synthetic.main.fragment_settings_user_understanding.*

class SettingsUserUnderstandingFragment : BaseFragment() {

    private val args: SettingsUserUnderstandingFragmentArgs by navArgs()
    private val chosenSetting: Int by lazy { args.chosenSetting }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_user_understanding, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseToolBar()
        setUserUnderstandingContent(chosenSetting)
        setContinueButtonOnClickListener()
        setUnderstandingSwitchOnCheckedChangeListener()
    }

    private fun initialiseToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun setUserUnderstandingContent(@Settings chosenSetting: Int) {
        when (chosenSetting) {
            PAYMENT_ADDRESS -> {
                settingsUserUnderstandingTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_payment_address_title)
                settingsUserUnderstandingContentTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_payment_address_content_title)
                settingsUserUnderstandingContentDescriptionTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_payment_address_content_description)
            }
            INVOICE_NAME -> {
                settingsUserUnderstandingTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_invoice_name_title)
                settingsUserUnderstandingContentTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_invoice_name_content_title)
                settingsUserUnderstandingContentDescriptionTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_invoice_name_content_description)
            }
            MASTER_PIN -> {
                settingsUserUnderstandingTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_master_pin_title)
                settingsUserUnderstandingContentTitleTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_master_pin_content_title)
                settingsUserUnderstandingContentDescriptionTextView.text =
                    resources.getText(R.string.settings_user_understand_fragment_master_pin_content_description)
            }
        }
    }

    private fun setContinueButtonOnClickListener() {
        settingsUserUnderstandingContinueButton.setOnClickListener {
            navigateToSetting(chosenSetting)
        }
    }

    private fun setUnderstandingSwitchOnCheckedChangeListener() {
        settingsUserUnderstandingContentUnderstandSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsUserUnderstandingContinueButton.backgroundTintList = setButtonColor(R.color.radixGreen)
                settingsUserUnderstandingContinueButton.isEnabled = true
            } else {
                settingsUserUnderstandingContinueButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
                settingsUserUnderstandingContinueButton.isEnabled = false
            }
        }
    }

    private fun navigateToSetting(@Settings chosenSetting: Int) {
        when (chosenSetting) {
            PAYMENT_ADDRESS -> {
                findNavController().navigate(
                    R.id.action_navigation_settings_user_understanding_to_navigation_settings_change_pay_to_address
                )
            }
            INVOICE_NAME -> {
                findNavController().navigate(
                    R.id.action_navigation_settings_user_understanding_to_navigation_settings_change_invoice_name
                )
            }
            MASTER_PIN -> {
                findNavController().navigate(
                    R.id.action_navigation_settings_user_understanding_to_navigation_settings_change_master_pin
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
