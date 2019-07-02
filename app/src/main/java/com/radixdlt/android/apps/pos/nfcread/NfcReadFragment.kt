package com.radixdlt.android.apps.pos.nfcread

import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.View
import com.radixdlt.android.apps.pos.ui.BaseFragment
import timber.log.Timber

abstract class NfcReadFragment : BaseFragment() {

    // Recommend NfcAdapter flags for reading from other Android devices. Indicates that this
    // activity is interested in NFC-A devices (including other Android devices), and that the
    // system should not check for the presence of NDEF-formatted data (e.g. Android Beam).
    private val readerFlags = NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK

    private var nfcWasEnabled: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableReaderMode()
    }

    override fun onPause() {
        super.onPause()
        nfcWasEnabled = NfcAdapter.getDefaultAdapter(activity).isEnabled
        if (nfcWasEnabled) {
            disableReaderMode()
        }
    }

    override fun onResume() {
        super.onResume()
        if (nfcWasEnabled) {
            enableReaderMode()
        }
    }

    private fun enableReaderMode() {
        Timber.i("Enabling reader mode")
        val nfc = NfcAdapter.getDefaultAdapter(activity)
        nfc.enableReaderMode(activity, getViewModel(), readerFlags, null)
    }

    private fun disableReaderMode() {
        Timber.i("Disabling reader mode")
        val nfc = NfcAdapter.getDefaultAdapter(activity)
        nfc.disableReaderMode(activity)
    }

    abstract fun getViewModel(): NfcAdapter.ReaderCallback
}
