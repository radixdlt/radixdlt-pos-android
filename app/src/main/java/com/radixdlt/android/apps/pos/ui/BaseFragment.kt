package com.radixdlt.android.apps.pos.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    lateinit var ctx: Context

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = view.context
    }

    fun setButtonColor(@ColorRes id: Int): ColorStateList? = ContextCompat.getColorStateList(ctx, id)
}
