package com.radixdlt.android.apps.pos.ui.payment.activity

import androidx.lifecycle.ViewModel
import com.radixdlt.android.apps.pos.util.VaultPreferences
import com.radixdlt.client.application.translate.data.receipt.Merchant
import com.radixdlt.client.application.translate.data.receipt.MerchantCategory
import com.radixdlt.client.application.translate.data.receipt.Purchase
import com.radixdlt.client.atommodel.accounts.RadixAddress
import com.radixdlt.client.core.atoms.UnsignedAtom

class PaymentViewModel : ViewModel() {
    lateinit var unsignedAtom: UnsignedAtom
    var incorrectPin: String = ""

    private val myShop = Merchant(
        "AlexMarcAngadBucks",
        RadixAddress.from(VaultPreferences.getVaultPaymentAddress()),
        MerchantCategory.RESTAURANT
    )
    lateinit var purchase: Purchase

    fun createNewPurchase() {
        purchase = Purchase(myShop)
    }
}
