package com.radixdlt.android.apps.pos.ui.view.edittext

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText

class BackPressedEditText : AppCompatEditText {

    private var onImeBack: BackEventListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            onImeBack?.invoke(this, this.text?.toString() ?: "")
        }
        return super.dispatchKeyEvent(event)
    }

    fun setBackEventListener(backEventListener: BackEventListener) {
        onImeBack = backEventListener
    }
}

typealias BackEventListener = (backPressedEditText: BackPressedEditText, text: String) -> Unit
