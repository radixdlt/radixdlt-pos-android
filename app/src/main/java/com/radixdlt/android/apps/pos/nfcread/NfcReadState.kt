package com.radixdlt.android.apps.pos.nfcread

sealed class NfcReadState {
    object NfcTagLost : NfcReadState()
    object Listening : NfcReadState()
    class Received(val message: String) : NfcReadState()
}
