package com.radixdlt.android.apps.pos.nfcread.viewmodel

import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.radixdlt.android.apps.pos.nfcread.NfcReadState
import org.bouncycastle.util.encoders.Hex
import timber.log.Timber
import java.io.IOException
import java.util.Arrays

open class NfcReadAddressViewModel : NfcReadViewModel() {

    private val _nfcReadState: MutableLiveData<NfcReadState> = MutableLiveData()

    val nfcReadState: LiveData<NfcReadState>
        get() = _nfcReadState

    /**
     * Callback when a new tag is discovered by the system.
     *
     *
     * Communication with the card should take place here.
     *
     * @param tag Discovered tag
     */
    override fun onTagDiscovered(tag: Tag?) {
        IsoDep.get(tag)?.let { isoDep ->
            try {

                try {
                    initialConnectionCommand(isoDep)
                } catch (e: TagLostException) {
                    onNfcTagLost()
                    e.printStackTrace()
                    return@let
                }

                val pubKeyResult: ByteArray
                try {
                    pubKeyResult = isoDep.transceive(SELECT_PUB_KEY)
                } catch (e: TagLostException) {
                    onNfcTagLost()
                    e.printStackTrace()
                    return@let
                }

                val resultLengthPubKey = pubKeyResult.size
                val statusWord = byteArrayOf(pubKeyResult[resultLengthPubKey - 2], pubKeyResult[resultLengthPubKey - 1])
                val payloadPubKey = pubKeyResult.copyOf(resultLengthPubKey - 2)

                if (Arrays.equals(SELECT_OK_SW, statusWord)) {
                    Timber.i("Received: %s", Hex.toHexString(payloadPubKey))
                    onPublicKeyReceived(Hex.toHexString(payloadPubKey))
                } else {
                    Timber.i("The statusWord = %s", Hex.toHexString(statusWord))
                }
            } catch (e: IOException) {
                Timber.e(e, "Error communicating with card: %s", e.toString())
            }
        }
    }

    private fun onPublicKeyReceived(publicKey: String) {
        _nfcReadState.postValue(NfcReadState.Received(publicKey))
    }

    private fun onNfcTagLost() {
        _nfcReadState.postValue(NfcReadState.NfcTagLost)
    }

    fun setNfcToListeningState() {
        _nfcReadState.value = NfcReadState.Listening
    }

    companion object {
        private val SELECT_PUB_KEY =
            byteArrayOf(0xB0.toByte(), 0x30.toByte(), 0x01.toByte(), 0x00.toByte(), 0xFF.toByte())
    }
}
