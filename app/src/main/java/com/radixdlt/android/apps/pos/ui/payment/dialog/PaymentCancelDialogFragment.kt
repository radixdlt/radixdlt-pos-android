package com.radixdlt.android.apps.pos.ui.payment.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.radixdlt.android.apps.pos.R

class PaymentCancelDialogFragment : AppCompatDialogFragment() {

    private lateinit var ctx: Context

    private var listener: PaymentCancelDialogListener? = null

    interface PaymentCancelDialogListener {
        fun onDialogPositiveClick(dialog: AppCompatDialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(ctx, R.style.AppThemeDialogLight)
            .setTitle(R.string.dialog_fragment_payment_cancel_title)
            .setMessage(R.string.dialog_fragment_payment_cancel_message)
            .setPositiveButton(R.string.dialog_fragment_payment_cancel_cancel_button) { _, _ ->
                listener?.let {
                    it.onDialogPositiveClick(this@PaymentCancelDialogFragment)
                    return@setPositiveButton
                }

                sendResult(Activity.RESULT_OK)
            }
            .setNegativeButton(R.string.dialog_fragment_payment_cancel_no_button) { _, _ -> dismiss() }
            .create()
    }

    private fun sendResult(resultCode: Int) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        if (context is PaymentCancelDialogListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
