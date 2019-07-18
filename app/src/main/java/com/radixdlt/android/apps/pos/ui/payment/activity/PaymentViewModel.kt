package com.radixdlt.android.apps.pos.ui.payment.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.client.core.atoms.UnsignedAtom
import com.radixdlt.client.core.network.websocket.WebSocketStatus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class PaymentViewModel : ViewModel() {

    private val radixApplicationAPI by RadixApplicationAPI
    private val compositeDisposable = CompositeDisposable()

    lateinit var unsignedAtom: UnsignedAtom
    var incorrectPin: String = ""

    private val _radixNetworkConnection: MutableLiveData<PaymentRadixNetworkState> = MutableLiveData()

    val radixNetworkConnection: LiveData<PaymentRadixNetworkState>
        get() = _radixNetworkConnection

    init {
        radixNetworkConnectionStatus()
    }

    private fun radixNetworkConnectionStatus() {
        radixApplicationAPI.networkState
            .distinct { it.nodeStates.values.first().status.name }
            .subscribe {
                val radixNetworkConnection = when (it.nodeStates.values.first().status) {
                    WebSocketStatus.CONNECTING -> PaymentRadixNetworkState.CONNECTING
                    WebSocketStatus.CONNECTED -> PaymentRadixNetworkState.CONNECTED
                    WebSocketStatus.FAILED -> PaymentRadixNetworkState.DISCONNECTED
                    WebSocketStatus.CLOSING -> PaymentRadixNetworkState.DISCONNECTED
                    WebSocketStatus.DISCONNECTED -> PaymentRadixNetworkState.DISCONNECTED
                    else -> PaymentRadixNetworkState.DISCONNECTED
                }
                _radixNetworkConnection.postValue(radixNetworkConnection)
            }.addTo(compositeDisposable)
    }
}
