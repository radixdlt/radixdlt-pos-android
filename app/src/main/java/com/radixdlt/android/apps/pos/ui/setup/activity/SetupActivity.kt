package com.radixdlt.android.apps.pos.ui.setup.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentActivity
import com.radixdlt.android.apps.pos.ui.setup.dialog.SetupQuitDialogFragment
import com.radixdlt.android.apps.pos.util.VaultPreferences
import net.skoumal.fragmentback.BackFragmentHelper
import org.jetbrains.anko.startActivity

class SetupActivity : AppCompatActivity(), SetupQuitDialogFragment.SetupQuitDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (
            VaultPreferences.getVaultPIN().isNotEmpty() &&
            VaultPreferences.getVaultPaymentAddress().isNotEmpty() &&
            VaultPreferences.getVaultInvoiceName().isNotEmpty()
        ) {
            startActivity<PaymentActivity>()
            finish()
            return
        }
        setTheme(R.style.AppThemeDark)
        setContentView(R.layout.activity_setup)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment_setup).navigateUp()

    override fun onBackPressed() {
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            findNavController(R.id.nav_host_fragment_setup).navigate(R.id.navigation_setup_quit)
        }
    }

    override fun onDialogPositiveClick(dialog: AppCompatDialogFragment) {
        finish()
    }
}
