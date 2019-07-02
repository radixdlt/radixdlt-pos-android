package com.radixdlt.android.apps.pos.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.radixdlt.android.apps.pos.R
import kotlinx.android.synthetic.main.dialog_progress.*

open class DelayedProgressDialog : AppCompatDialogFragment() {

    private var startedShowing: Boolean = false
    private var startMillisecond: Long = 0
    private var stopMillisecond: Long = 0

    private var fm: FragmentManager? = null
    private var fragmentTAG: String? = null

    private var showHandler: Handler? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val v = View.inflate(activity, R.layout.dialog_progress, null)
        builder.setView(v)
        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        if (dialog?.window != null) {
            val px = (PROGRESS_CONTENT_WIDTH_DP * resources.displayMetrics.density).toInt()
            dialog?.window!!.setLayout(px, FrameLayout.LayoutParams.WRAP_CONTENT)
            dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun show(fm: FragmentManager, tag: String?) {
        if (isAdded)
            return

        this.fm = fm
        this.fragmentTAG = tag

        startMillisecond = System.currentTimeMillis()
        startedShowing = false
        stopMillisecond = java.lang.Long.MAX_VALUE

        showHandler = Handler()
        showHandler!!.postDelayed({
            // only show if not already cancelled
            if (stopMillisecond > System.currentTimeMillis()) {
                showDialogAfterDelay()
            }
        }, DELAY_MILLISECOND)
    }

    private fun showDialogAfterDelay() {
        startedShowing = true

        val dialogFragment = fm?.findFragmentByTag(fragmentTAG) as DialogFragment?
        if (dialogFragment != null) {
            fm?.beginTransaction()?.show(dialogFragment)?.commitAllowingStateLoss()
        } else {
            val ft = fm?.beginTransaction()
            ft?.add(this, fragmentTAG)
            ft?.commitAllowingStateLoss()
        }
    }

    fun cancel() {
        if (showHandler == null) return

        stopMillisecond = System.currentTimeMillis()
        showHandler!!.removeCallbacksAndMessages(null)

        if (startedShowing) {
            if (progressBar != null) {
                cancelWhenShowing()
            } else {
                cancelWhenNotShowing()
            }
        } else {
            dismiss()
        }
    }

    private fun cancelWhenShowing() {
        if (stopMillisecond < startMillisecond + DELAY_MILLISECOND + MINIMUM_SHOW_DURATION_MILLISECOND) {
            val handler = Handler()
            handler.postDelayed({ dismiss() }, MINIMUM_SHOW_DURATION_MILLISECOND)
        } else {
            dismiss()
        }
    }

    private fun cancelWhenNotShowing() {
        val handler = Handler()
        handler.postDelayed({ dismiss() }, DELAY_MILLISECOND)
    }

    override fun dismiss() {
        val ft = fm?.beginTransaction()
        ft?.remove(this)
        ft?.commitAllowingStateLoss()
    }

    companion object {
        private const val DELAY_MILLISECOND = 100L
        private const val MINIMUM_SHOW_DURATION_MILLISECOND = 300L
        private const val PROGRESS_CONTENT_WIDTH_DP = 200
    }
}
