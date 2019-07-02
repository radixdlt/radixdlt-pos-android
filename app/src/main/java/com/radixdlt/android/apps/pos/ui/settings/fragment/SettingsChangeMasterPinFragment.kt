package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.android.apps.pos.util.replaceWithAsterisk
import kotlinx.android.synthetic.main.fragment_settings_change_master_pin.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.skoumal.fragmentback.BackFragment

class SettingsChangeMasterPinFragment : Fragment(), BackFragment {

    private lateinit var pinSet: String
    private val masterPin: String = VaultPreferences.getVaultPIN()
    private var setupPinState: SettingsChangePinState = SettingsChangePinState.OLD

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_change_master_pin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseToolBar()
        setOnLayoutChangeListener(view)
        setOnKeypadTextChangeListener()
    }

    private fun setOnKeypadTextChangeListener() {
        settingsChangeMasterPinKeypad.setOnTextChangeListener { pin, _ ->

            hideErrorMessageIfVisible()

            val hiddenPin = pin.replaceRange(0, pin.length, replaceWithAsterisk(pin))
            settingsChangeMasterPinPinTextView.text = hiddenPin

            if (pin.length == 4 && setupPinState == SettingsChangePinState.OLD) {
                checkMasterPin(pin)
            } else if (pin.length == 4 && setupPinState == SettingsChangePinState.NEW) {
                displayInputConfirmPinState(pin)
            } else if (pin.length == 4 && setupPinState == SettingsChangePinState.CONFIRM) {
                validateNewPinMatchesPreviousInput(pin)
            }
        }
    }

    private fun hideErrorMessageIfVisible() {
        if (settingsChangeMasterPinPinErrorTextView.visibility == View.VISIBLE) {
            settingsChangeMasterPinPinErrorTextView.visibility = View.GONE
        }
    }

    private fun isSamePin(pin: String) = pin == pinSet

    private fun isMasterPin(pin: String) = pin == masterPin

    private fun checkMasterPin(pin: String) {
        if (isMasterPin(pin)) {
            displayInputNewPinState(pin)
        } else {
            displayIncorrectPinMessage(getString(R.string.settings_change_master_pin_fragment_incorrect_pin))
        }
    }

    private fun validateNewPinMatchesPreviousInput(pin: String) {
        if (isSamePin(pin)) {
            updateMasterPin(pin)
        } else {
            displayIncorrectPinMessage(getString(R.string.settings_change_master_pin_fragment_pin_not_match))
        }
    }

    private fun updateMasterPin(pin: String) {
        VaultPreferences.setVaultPIN(pin)
        navigateToSettingsUpdated()
    }

    private fun navigateToSettingsUpdated() {
        val action = SettingsChangeMasterPinFragmentDirections
            .actionNavigationSettingsChangeMasterPinToNavigationSettingsUpdated()
        findNavController().navigate(action)
    }

    private fun displayInputConfirmPinState(pin: String) {
        settingsChangeMasterPinHeaderTextView.text = getString(R.string.settings_change_master_pin_fragment_confirm_pin)
        setupPinState = SettingsChangePinState.CONFIRM
        clearHiddenPinWithDelay()
        pinSet = pin
        settingsChangeMasterPinKeypad.clearDigits()
    }

    private fun displayIncorrectPinMessage(message: String) {
        settingsChangeMasterPinPinErrorTextView.visibility = View.VISIBLE
        settingsChangeMasterPinPinErrorTextView.text = message
        clearHiddenPinWithDelay()
        settingsChangeMasterPinKeypad.clearDigits()
    }

    private fun displayInputNewPinState(pin: String) {
        settingsChangeMasterPinHeaderTextView.text = getString(R.string.settings_change_master_pin_fragment_new_pin)
        setupPinState = SettingsChangePinState.NEW
        clearHiddenPinWithDelay()
        pinSet = pin
        settingsChangeMasterPinKeypad.clearDigits()
    }

    private fun setOnLayoutChangeListener(view: View) {
        view.doOnLayout {
            val params = settingsChangeMasterPinPinTextView.layoutParams as ViewGroup.LayoutParams
            params.width = settingsChangeMasterPinPinDisabledTextView.width
            settingsChangeMasterPinPinTextView.layoutParams = params
        }
    }

    private fun initialiseToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun clearHiddenPinWithDelay() {
        lifecycleScope.launch {
            delay(200)
            settingsChangeMasterPinPinTextView.text = ""
        }
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        findNavController().navigate(
            R.id.action_navigation_settings_change_master_pin_to_navigation_settings
        )
        return true
    }

    private enum class SettingsChangePinState {
        OLD, NEW, CONFIRM
    }
}
