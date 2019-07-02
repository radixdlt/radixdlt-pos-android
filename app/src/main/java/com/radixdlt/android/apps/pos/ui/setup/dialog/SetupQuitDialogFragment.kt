package com.radixdlt.android.apps.pos.ui.setup.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.radixdlt.android.apps.pos.R

class SetupQuitDialogFragment : AppCompatDialogFragment() {

    private lateinit var ctx: Context

    private var listener: SetupQuitDialogListener? = null

    interface SetupQuitDialogListener {
        fun onDialogPositiveClick(dialog: AppCompatDialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(ctx, R.style.AppThemeDialog)
            .setTitle(R.string.setup_quit_dialog_fragment_title)
            .setMessage(R.string.setup_quit_dialog_fragment_message)
            .setPositiveButton(R.string.setup_quit_dialog_fragment_quit_button) { _, _ ->
                listener?.let {
                    it.onDialogPositiveClick(this@SetupQuitDialogFragment)
                    return@setPositiveButton
                }

                sendResult(Activity.RESULT_OK)
            }
            .setNegativeButton(R.string.setup_quit_dialog_fragment_no_button) { _, _ -> dismiss() }
            .create()
    }

    private fun sendResult(resultCode: Int) {
        targetFragment?.onActivityResult(targetRequestCode, resultCode, null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
        if (context is SetupQuitDialogListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
