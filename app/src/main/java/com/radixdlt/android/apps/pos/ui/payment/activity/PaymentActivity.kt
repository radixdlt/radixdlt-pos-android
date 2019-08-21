package com.radixdlt.android.apps.pos.ui.payment.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.payment.dialog.PaymentCancelDialogFragment
import com.radixdlt.android.apps.pos.util.VaultPreferences
import kotlinx.android.synthetic.main.tool_bar_white.*
import net.skoumal.fragmentback.BackFragmentHelper

class PaymentActivity : AppCompatActivity(), PaymentCancelDialogFragment.PaymentCancelDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initialiseToolBar()
        initialisePaymentViewModel()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment_payment).navigateUp()

    private fun initialiseToolBar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initialisePaymentViewModel() {
        ViewModelProviders.of(this)[PaymentViewModel::class.java]
    }

    override fun onDialogPositiveClick(dialog: AppCompatDialogFragment) {
        findNavController(R.id.nav_host_fragment_payment).popBackStack(R.id.navigation_payment_start, false)
    }

    override fun onBackPressed() {
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        toolbarTitleTextView.text = VaultPreferences.getVaultInvoiceName()
    }
}
