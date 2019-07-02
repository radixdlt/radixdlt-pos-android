package com.radixdlt.android.apps.pos.ui.payment.pin

import com.radixdlt.client.core.atoms.UnsignedAtom

sealed class PaymentPinAtomBuildingState {
    object Error : PaymentPinAtomBuildingState()
    object InsufficientFunds : PaymentPinAtomBuildingState()
    class UnsignedAtomBuilt(val unsignedAtom: UnsignedAtom) : PaymentPinAtomBuildingState()
}
