package com.radixdlt.android.apps.pos.ui.payment.pin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.android.apps.pos.util.TOKEN_REFERENCE_ADDRESS
import com.radixdlt.android.apps.pos.util.TOKEN_REFERENCE_SYMBOL
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.client.application.translate.tokens.InsufficientFundsException
import com.radixdlt.client.application.translate.tokens.TransferTokensAction
import com.radixdlt.client.atommodel.accounts.RadixAddress
import com.radixdlt.client.core.atoms.UnsignedAtom
import com.radixdlt.client.core.atoms.particles.RRI
import com.radixdlt.client.core.crypto.ECPublicKey
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import org.bouncycastle.util.encoders.Hex
import org.radix.utils.RadixConstants
import timber.log.Timber

class PaymentPinViewModel : ViewModel() {

    private val radixApplicationAPI by RadixApplicationAPI

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val _paymentPinAtomBuildingState: MutableLiveData<PaymentPinAtomBuildingState> = MutableLiveData()

    val paymentPinAtomBuildingState: LiveData<PaymentPinAtomBuildingState>
        get() = _paymentPinAtomBuildingState

    fun buildAtom(publicKey: String, amountText: String, reference: String) {
        var amount = amountText
        if (amount.contains(",")) {
            amount = amount.replace(",", ".")
        }

        val ecPublicKey = ECPublicKey(Hex.decode(publicKey))
        val radixAddress = radixApplicationAPI.getAddress(ecPublicKey)

        radixApplicationAPI
            .pullOnce(radixAddress)
            .subscribeOn(Schedulers.io())
            .subscribe {

            val transaction = radixApplicationAPI.createTransaction()

            try {
                transaction.stage(TransferTokensAction.create(
                    RRI.of(
                        RadixAddress.from(TOKEN_REFERENCE_ADDRESS), TOKEN_REFERENCE_SYMBOL
                    ),
                    radixAddress,
                    RadixAddress.from(VaultPreferences.getVaultPaymentAddress()),
                    amount.toBigDecimal(),
                    if (reference.isNotEmpty()) reference.toByteArray(RadixConstants.STANDARD_CHARSET) else null
                ))
                builtAtom(transaction.buildAtom())
            } catch (t: Throwable) {
                errorBuildingAtom(t)
            }
        }.addTo(compositeDisposable)
    }

    private fun builtAtom(unsignedAtom: UnsignedAtom) {
        _paymentPinAtomBuildingState.postValue(PaymentPinAtomBuildingState.UnsignedAtomBuilt(unsignedAtom))
    }

    private fun errorBuildingAtom(exception: Throwable) {
        if (exception is InsufficientFundsException) {
            _paymentPinAtomBuildingState.postValue(PaymentPinAtomBuildingState.InsufficientFunds)
        } else {
            _paymentPinAtomBuildingState.postValue(PaymentPinAtomBuildingState.Error)
        }

        Timber.e("Error building atom: $exception")
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }
}
