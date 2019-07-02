package com.radixdlt.android.apps.pos.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.net.ConnectivityManager
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.radixdlt.android.apps.pos.R
import org.jetbrains.anko.internals.AnkoInternals
import org.jetbrains.anko.toast
import java.math.BigDecimal

fun Fragment.hideKeyboard() = view?.let { activity?.hideKeyboard(it) }

fun Activity.hideKeyboard() = hideKeyboard(if (currentFocus == null) View(this) else currentFocus)

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.toast(@StringRes message: Int) = view?.let { activity?.toast(message) }

fun Fragment.toast(message: CharSequence) = view?.let { activity?.toast(message) }

fun Fragment.finish() = view?.let { activity?.finish() }

fun Activity.getRootView(): View = findViewById<View>(android.R.id.content)

fun Context.convertDpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this.resources.displayMetrics
    )
}

fun Fragment.isKeyboardOpen(): Boolean = view?.let { return isKeyboardOpen(it.rootView) } ?: false

fun Activity.isKeyboardOpen(view: View): Boolean = isKeyboardOpen(view)

private fun isKeyboardOpen(rootView: View): Boolean {
    val visibleBounds = Rect()
    rootView.getWindowVisibleDisplayFrame(visibleBounds)
    val heightDiff = rootView.height - visibleBounds.height()
    val marginOfError = Math.round(rootView.context.convertDpToPx(50f))
    return heightDiff > marginOfError
}

fun Fragment.isKeyboardClosed(): Boolean = !this.isKeyboardOpen()

fun Activity.isKeyboardClosed(view: View): Boolean = !this.isKeyboardOpen(view)

inline fun <reified T : Activity> Fragment.startActivity(vararg params: Pair<String, Any?>) {
    activity?.apply { AnkoInternals.internalStartActivity(this, T::class.java, params) }
}

/**
 * There is a bug in < jdk 8 which doesn't remove trailing zeros
 * for zero value and since Android doesn't fully utilize java 8
 * where the bug is fixed, the problem persists.
 *
 * Checks if value is 0 and return 0 as a whole value else strips normally.
 *
 * @return BigDecimal without trailing zeros including zero values.
 * @see <a href="https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6480539">https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6480539</a>
 */
fun BigDecimal.fixedStripTrailingZeros(): BigDecimal {
    val zero = BigDecimal.ZERO
    return if (this.compareTo(zero) == 0) {
        zero
    } else {
        this.stripTrailingZeros()
    }
}

fun Activity.isConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return cm?.activeNetworkInfo?.isConnected ?: false
}

fun Fragment.isConnected(): Boolean {
    val cm = view?.let {
        activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    }
    return cm?.activeNetworkInfo?.isConnected ?: false
}

fun Fragment.replaceWithAsterisk(pin: String): String {
    return when (pin.length) {
        1 -> getString(R.string.radix_pos_common_pin_one_asterisk)
        2 -> getString(R.string.radix_pos_common_pin_two_asterisk)
        3 -> getString(R.string.radix_pos_common_pin_three_asterisk)
        4 -> getString(R.string.radix_pos_common_pin_four_asterisk)
        else -> ""
    }
}

fun MaterialButton.setButtonDrawableOnTopOfText() {
    val pasteDrawable = TextViewCompat.getCompoundDrawablesRelative(this)[0]
    TextViewCompat.setCompoundDrawablesRelative(this, null, pasteDrawable, null, null)
}
