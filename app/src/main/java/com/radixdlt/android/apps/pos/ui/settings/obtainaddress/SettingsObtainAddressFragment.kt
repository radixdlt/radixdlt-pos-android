package com.radixdlt.android.apps.pos.ui.settings.obtainaddress

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.android.apps.pos.nfcread.NfcReadFragment
import com.radixdlt.android.apps.pos.nfcread.NfcReadState

import com.radixdlt.client.core.crypto.ECPublicKey
import kotlinx.android.synthetic.main.fragment_settings_obtain_address.*
import org.bouncycastle.util.encoders.Hex

class SettingsObtainAddressFragment : NfcReadFragment() {

    private val radixApplicationAPI by RadixApplicationAPI

    private val settingsObtainAddressViewModel: SettingsObtainAddressViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_obtain_address, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseToolBar()
        observeViewModel()
    }

    private fun initialiseToolBar() {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun observeViewModel() {
        settingsObtainAddressViewModel.nfcReadState.observe(::getLifecycle, ::onPublicKeyReceived)
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

    override fun getViewModel(): NfcAdapter.ReaderCallback {
        return settingsObtainAddressViewModel
    }

    private fun onPublicKeyReceived(nfcReadState: NfcReadState) {
        when (nfcReadState) {
            is NfcReadState.Received -> showCardAddress(nfcReadState.message)
        }
    }

    private fun showCardAddress(cardAddress: String) {
        val radixAddress = radixApplicationAPI.getAddress(
            ECPublicKey(Hex.decode(cardAddress))
        ).toString()
        addressTextView.text = radixAddress
    }
}
