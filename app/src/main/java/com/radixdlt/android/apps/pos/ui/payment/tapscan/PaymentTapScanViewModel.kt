package com.radixdlt.android.apps.pos.ui.payment.tapscan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.android.apps.pos.nfcread.viewmodel.NfcReadAddressViewModel
import com.radixdlt.android.apps.pos.util.TOKEN_REFERENCE_ADDRESS
import com.radixdlt.android.apps.pos.util.TOKEN_REFERENCE_SYMBOL
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.client.atommodel.accounts.RadixAddress
import com.radixdlt.client.core.atoms.particles.RRI
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.math.BigDecimal

class PaymentTapScanViewModel : NfcReadAddressViewModel() {

    private lateinit var transactionAmount: String

    private val radixApplicationAPI by RadixApplicationAPI

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var originalTokenBalance: BigDecimal = BigDecimal.ZERO

    private var balanceMonitoringState = TokenBalance.INITIAL

    private val _paymentTapScanState: MutableLiveData<PaymentTapScanState> = MutableLiveData()

    private val acceptedToken = RRI.of(
        RadixAddress.from(TOKEN_REFERENCE_ADDRESS), TOKEN_REFERENCE_SYMBOL
    )

    val paymentTapScanState: LiveData<PaymentTapScanState>
        get() = _paymentTapScanState

    fun monitorBalance(amount: String) {
        transactionAmount = amount
        radixApplicationAPI.observeBalances(RadixAddress.from(VaultPreferences.getVaultPaymentAddress()))
            .subscribeOn(Schedulers.io())
            .subscribe(::listenToBalanceChanges, ::showErrorStackTrace)
            .addTo(compositeDisposable)
    }

    private fun listenToBalanceChanges(balance: MutableMap<RRI, BigDecimal>) {
        val tokenBalance = balance[acceptedToken] ?: BigDecimal.ZERO
        when (balanceMonitoringState) {
            TokenBalance.INITIAL -> saveOriginalBalanceAndListen(tokenBalance)
            TokenBalance.LISTENING -> checkIfExpectedBalance(tokenBalance)
        }
    }

    private fun saveOriginalBalanceAndListen(tokenBalance: BigDecimal) {
        originalTokenBalance = tokenBalance
        balanceMonitoringState = TokenBalance.LISTENING
    }

    private fun checkIfExpectedBalance(tokenBalance: BigDecimal) {
        if (sumOriginalBalanceWithTransactionAmount() == tokenBalance.stripTrailingZeros()) {
            _paymentTapScanState.value = PaymentTapScanState.RECEIVED
            balanceMonitoringState = TokenBalance.INITIAL
        }
    }

    private fun sumOriginalBalanceWithTransactionAmount(): BigDecimal =
        originalTokenBalance.stripTrailingZeros() + transactionAmount.toBigDecimal().stripTrailingZeros()

    private fun showErrorStackTrace(it: Throwable?) {
        Timber.e(it)
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }

    private enum class TokenBalance {
        INITIAL, LISTENING
    }
}
