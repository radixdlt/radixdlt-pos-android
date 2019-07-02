package com.radixdlt.android.apps.pos.ui.settings.dialog

import android.os.Bundle
import android.view.View
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.dialog.DelayedProgressDialog
import kotlinx.android.synthetic.main.dialog_progress.*

class ResetDataProgressDialog : DelayedProgressDialog() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageTextView.text = activity?.getString(R.string.progress_dialog_reset_data_resetting_data)
    }

    companion object {
        const val TAG = "RESET_DATA_PROGRESS_DIALOG"
    }
}
