package com.radixdlt.android.apps.pos.nfcread.viewmodel

import android.nfc.NfcAdapter
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import androidx.lifecycle.ViewModel
import org.bouncycastle.util.encoders.Hex
import timber.log.Timber
import java.io.IOException

abstract class NfcReadViewModel : ViewModel(), NfcAdapter.ReaderCallback {

    @Throws(IOException::class, TagLostException::class)
    fun initialConnectionCommand(isoDep: IsoDep) {
        // Connect to the remote NFC device
        isoDep.connect()
        // Build SELECT AID command for our loyalty card service.
        // This command tells the remote device which service we wish to communicate with.
        Timber.i("Requesting remote AID: %s", RADIX_CARD_AID)
        Timber.d("Extended length supported: %s", isoDep.isExtendedLengthApduSupported)
        Timber.d("Extended max length: %s", isoDep.maxTransceiveLength)
        val command = buildSelectApdu(RADIX_CARD_AID)
        // Send command to remote device
        Timber.i("Sending: %s", Hex.toHexString(command))
        isoDep.transceive(command)
    }

    /**
     * Build APDU for SELECT AID command. This command indicates which service a reader is
     * interested in communicating with. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @return APDU for SELECT AID command
     */
    private fun buildSelectApdu(aid: String): ByteArray {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        return Hex.decode(SELECT_APDU_HEADER + String.format("%02X", aid.length / 2) + aid)
    }

    companion object {
        // AID for our loyalty card service.
        private const val RADIX_CARD_AID = "DEADBEEF7901"
        // ISO-DEP command HEADER for selecting an AID.
        // Format: [Class | Instruction | Parameter 1 | Parameter 2]
        private const val SELECT_APDU_HEADER = "00A40400"
        // "OK" status word sent in response to SELECT AID command (0x9000)
        val SELECT_OK_SW = byteArrayOf(0x90.toByte(), 0x00.toByte())
    }
}
