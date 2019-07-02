package com.radixdlt.android.apps.pos.ui.setup.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.util.finish
import com.radixdlt.android.apps.pos.util.startActivity
import com.radixdlt.android.apps.pos.ui.payment.activity.PaymentActivity
import kotlinx.android.synthetic.main.fragment_setup_complete.*
import net.skoumal.fragmentback.BackFragment

class SetupCompleteFragment : Fragment(), BackFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setup_complete, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCompleteFinishButton.setOnClickListener {
            finish()
            startActivity<PaymentActivity>()
        }

        setupCompleteSuccessAnimationView.playAnimation()
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        finish()
        return true
    }
}
