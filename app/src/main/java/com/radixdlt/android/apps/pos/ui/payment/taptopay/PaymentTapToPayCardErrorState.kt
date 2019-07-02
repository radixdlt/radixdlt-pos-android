package com.radixdlt.android.apps.pos.ui.payment.taptopay

sealed class PaymentTapToPayCardErrorState {
    object Blocked : PaymentTapToPayCardErrorState()
    class PinError(val code: Int) : PaymentTapToPayCardErrorState()
}
