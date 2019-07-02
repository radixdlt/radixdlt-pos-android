package com.radixdlt.android.apps.pos.ui.payment.activity

import androidx.lifecycle.ViewModel
import com.radixdlt.client.core.atoms.UnsignedAtom

class PaymentViewModel : ViewModel() {
    lateinit var unsignedAtom: UnsignedAtom
    var incorrectPin: String = ""
}
