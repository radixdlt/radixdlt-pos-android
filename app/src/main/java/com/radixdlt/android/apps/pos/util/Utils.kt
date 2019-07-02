package com.radixdlt.android.apps.pos.util

import android.animation.Animator
import com.radixdlt.client.core.atoms.RadixHash
import com.radixdlt.client.core.util.Base58
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

fun formatNorwegianKrone(money: BigDecimal): String {
    val locale = Locale("no", "NO")
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(money)
}

fun formatUSADollar(money: BigDecimal): String {
    val locale = Locale("en", "US")
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(money)
}

fun isRadixAddress(addressBase58: String): Boolean {

    if (addressBase58.length < 5) return false

    val raw: ByteArray
    try {
        raw = Base58.fromBase58(addressBase58)
    } catch (e: IllegalArgumentException) {
        return false
    }
    val check = RadixHash.of(raw, 0, raw.size - 4)
    for (i in 0..3) {
        if (check.get(i) != raw[raw.size - 4 + i]) {
            return false
        }
    }

    return true
}

object EmptyAnimatorListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {}
    override fun onAnimationEnd(animation: Animator?) {}
    override fun onAnimationCancel(animation: Animator?) {}
    override fun onAnimationStart(animation: Animator?) {}
}
