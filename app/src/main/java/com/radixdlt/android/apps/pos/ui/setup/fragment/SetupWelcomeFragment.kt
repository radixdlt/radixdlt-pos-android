package com.radixdlt.android.apps.pos.ui.setup.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import com.radixdlt.android.apps.pos.ui.BaseFragment
import com.radixdlt.android.apps.pos.util.finish
import kotlinx.android.synthetic.main.fragment_setup_welcome.*
import net.skoumal.fragmentback.BackFragment

class SetupWelcomeFragment : BaseFragment(), BackFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_setup_welcome, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWelcomeStartButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_setup_welcome_to_navigation_setup_pin
            )
        }

        setupWelcomeUnderstandSwitch.setOnCheckedChangeListener { _, isChecked ->
            setupWelcomeStartButton.isEnabled = isChecked
            when {
                isChecked -> setupWelcomeStartButton.backgroundTintList = setButtonColor(R.color.radixGreen)
                else -> setupWelcomeStartButton.backgroundTintList = setButtonColor(R.color.disabledLightGray)
            }
        }
    }

    override fun getBackPriority(): Int {
        return BackFragment.NORMAL_BACK_PRIORITY
    }

    override fun onBackPressed(): Boolean {
        finish()
        return true
    }
}
