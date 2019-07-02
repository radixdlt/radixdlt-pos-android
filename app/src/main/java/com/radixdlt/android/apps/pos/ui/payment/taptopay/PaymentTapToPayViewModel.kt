package com.radixdlt.android.apps.pos.ui.payment.taptopay

import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.android.apps.pos.nfcread.NfcReadState
import com.radixdlt.android.apps.pos.nfcread.viewmodel.NfcReadViewModel
import com.radixdlt.android.apps.pos.util.CARD_BLOCKED
import com.radixdlt.android.apps.pos.util.CardStatusCode
import com.radixdlt.android.apps.pos.util.PIN_ERROR_FIRST_ATTEMPT
import com.radixdlt.android.apps.pos.util.PIN_ERROR_ONE_ATTEMPT_REMAINING
import com.radixdlt.android.apps.pos.util.PIN_ERROR_SECOND_ATTEMPT
import com.radixdlt.android.apps.pos.util.PIN_ERROR_TWO_ATTEMPTS_REMAINING
import com.radixdlt.android.apps.pos.util.SELECT_OK
import com.radixdlt.client.core.atoms.RadixHash
import com.radixdlt.client.core.atoms.UnsignedAtom
import com.radixdlt.client.core.crypto.ECSignature
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.bouncycastle.util.encoders.Hex
import timber.log.Timber
import java.io.IOException
import java.util.Arrays

class PaymentTapToPayViewModel(
    private val unsignedAtom: UnsignedAtom,
    private val pin: String,
    private val publicKey: String
) : NfcReadViewModel() {

    private val radixApplicationAPI by RadixApplicationAPI

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val _nfcReadState: MutableLiveData<NfcReadState> = MutableLiveData()

    val nfcReadState: LiveData<NfcReadState>
        get() = _nfcReadState

    private val _cardErrorState: MutableLiveData<PaymentTapToPayCardErrorState> = MutableLiveData()

    val cardErrorState: LiveData<PaymentTapToPayCardErrorState>
        get() = _cardErrorState

    private val _paymentTapToPaySubmitAtomState: MutableLiveData<PaymentTapToPaySubmitAtomState> = MutableLiveData()

    val paymentTapToPaySubmitAtomState: LiveData<PaymentTapToPaySubmitAtomState>
        get() = _paymentTapToPaySubmitAtomState

    private var payloadSignature: ByteArray? = null

    override fun onTagDiscovered(tag: Tag?) {
        IsoDep.get(tag)?.let { isoDep ->
            try {
                // Connect to the remote NFC device

                try {
                    initialConnectionCommand(isoDep)
                } catch (e: TagLostException) {
                    onNfcTagLost()
                    e.printStackTrace()
                    return@let
                }

                val authCommandResult: ByteArray
                try {
                    authCommandResult = isoDep.transceive(authCommand())
                } catch (e: TagLostException) {
                    onNfcTagLost()
                    e.printStackTrace()
                    return@let
                }

                val statusWord = byteArrayOf(
                    authCommandResult[authCommandResult.size - 2],
                    authCommandResult[authCommandResult.size - 1]
                )

                // will receive length, 2 bytes = error
                // I will receive the signature, 1 thing trim first byte
                if (Arrays.equals(SELECT_OK_SW, statusWord)) {
                    // The remote NFC device will immediately respond with its stored account number
                    Timber.i("Received OK from Pin: %s", Hex.toHexString(statusWord))
                    val signCommand = signCommand()
                    Timber.d(
                        "Atom length %s signCommandHex %s",
                        unsignedAtom.rawAtom.toDson().size,
                        Hex.toHexString(signCommand)
                    )

                    val signCommandResult: ByteArray
                    try {
                        signCommandResult = isoDep.transceive(signCommand)
                    } catch (e: TagLostException) {
                        onNfcTagLost()
                        e.printStackTrace()
                        return@let
                    }

                    Timber.i("SignCommandResult: %s", Hex.toHexString(signCommandResult))
                    val signStatusWord = byteArrayOf(
                        signCommandResult[signCommandResult.size - 2],
                        signCommandResult[signCommandResult.size - 1]
                    )

                    if (Arrays.equals(SELECT_OK_SW, signStatusWord)) {
                        payloadSignature = signCommandResult.copyOf(signCommandResult.size - 2)
                        Timber.d("Ok received unsignedAtom signed %s", Hex.toHexString(payloadSignature))
                        onDataReceived(Hex.toHexString(signStatusWord))
                    }
                } else {
                    onDataReceived(Hex.toHexString(statusWord))
                    Timber.i("The statusWord = %s", Hex.toHexString(statusWord))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun onDataReceived(@CardStatusCode data: String) {
        when (data) {
            PIN_ERROR_TWO_ATTEMPTS_REMAINING -> setCardErrorState(
                PaymentTapToPayCardErrorState.PinError(
                    PIN_ERROR_FIRST_ATTEMPT
                )
            )
            PIN_ERROR_ONE_ATTEMPT_REMAINING -> setCardErrorState(
                PaymentTapToPayCardErrorState.PinError(
                    PIN_ERROR_SECOND_ATTEMPT
                )
            )
            CARD_BLOCKED -> setCardErrorState(PaymentTapToPayCardErrorState.Blocked)
            SELECT_OK -> readyToSubmitAtom()
        }
    }

    private fun onNfcTagLost() {
        setNfcReaderState(NfcReadState.NfcTagLost)
    }

    private fun setCardErrorState(value: PaymentTapToPayCardErrorState) {
        _cardErrorState.postValue(value)
    }

    private fun setNfcReaderState(value: NfcReadState) {
        _nfcReadState.postValue(value)
    }

    private fun setAtomSubmissionState(value: PaymentTapToPaySubmitAtomState) {
        _paymentTapToPaySubmitAtomState.postValue(value)
    }

    fun atomSubmissionStateInitial() {
        setAtomSubmissionState(PaymentTapToPaySubmitAtomState.INITIAL)
    }

    private fun readyToSubmitAtom() {
        setAtomSubmissionState(PaymentTapToPaySubmitAtomState.READY)
    }

    private fun atomSubmitted() {
        Timber.d("Checking atomSubmitted")
        setAtomSubmissionState(PaymentTapToPaySubmitAtomState.SUBMITTED)
    }

    private fun atomSubmissionError(t: Throwable) {
        setAtomSubmissionState(PaymentTapToPaySubmitAtomState.ERROR)
        Timber.d("Checking atomSubmissionError")
        Timber.e(t)
    }

    private fun commandAPDU(cla: Byte, ins: Byte, p1: Byte, p2: Byte, data: ByteArray, le: Byte): ByteArray {
        val commandApduBytes: ByteArray

        // Create extended length APDU for data payloads over 256 bytes
        if (data.size > 0xFF) {
            commandApduBytes = ByteArray(data.size + 9)
            commandApduBytes[0] = cla
            commandApduBytes[1] = ins
            commandApduBytes[2] = p1
            commandApduBytes[3] = p2
            commandApduBytes[4] = 0x00
            commandApduBytes[5] = (data.size shr 8 and 0xFF).toByte()
            commandApduBytes[6] = (data.size and 0xFF).toByte()
            System.arraycopy(data, 0, commandApduBytes, 7, data.size)
            commandApduBytes[commandApduBytes.size - 2] = 0x00
            commandApduBytes[commandApduBytes.size - 1] = le
        } else {
            commandApduBytes = ByteArray(data.size + 6)
            commandApduBytes[0] = cla
            commandApduBytes[1] = ins
            commandApduBytes[2] = p1
            commandApduBytes[3] = p2
            commandApduBytes[4] = data.size.toByte()
            System.arraycopy(data, 0, commandApduBytes, 5, data.size)
            commandApduBytes[commandApduBytes.size - 1] = le
        } // Create standard length APDU for data payloads under 256 bytes

        return commandApduBytes
    }

    private fun authCommand(): ByteArray {
        return commandAPDU(
            cla,
            auth,
            0,
            0,
            pin.toByteArray(),
            pin.toByteArray().size.toByte()
        )
    }

    private fun signCommand(): ByteArray {
        return commandAPDU(
            cla,
            sign,
            0.toByte(),
            0.toByte(),
            unsignedAtom.rawAtom.toDson(),
            0xFF.toByte()
        )
    }

    fun submitAtom() {

        setAtomSubmissionState(PaymentTapToPaySubmitAtomState.LOADING)

        // Receive the OK
        // Get unsignedAtom and send away to network
        val ecSignature = ECSignature.decodeFromDER(payloadSignature)
        val signedAtom = unsignedAtom.sign(ecSignature, RadixHash.of(Hex.decode(publicKey)).toEUID())

        Timber.d("Submitting")

        radixApplicationAPI.submitAtom(signedAtom)
            .toCompletable()
            .subscribeOn(Schedulers.io())
            .subscribe(::atomSubmitted, ::atomSubmissionError)
            .addTo(compositeDisposable)
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }

    companion object {
        private const val cla = 0xB0.toByte()
        private const val auth = 0x20.toByte()
        private const val sign = 0x50.toByte()
    }
}

class PaymentTapToPayViewModelFactory(
    private val unsignedAtom: UnsignedAtom,
    private val pin: String,
    private val publicKey: String
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentTapToPayViewModel(unsignedAtom, pin, publicKey) as T
    }
}
