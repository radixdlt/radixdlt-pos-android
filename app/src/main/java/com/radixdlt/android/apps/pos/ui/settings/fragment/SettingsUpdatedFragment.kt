package com.radixdlt.android.apps.pos.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.radixdlt.android.apps.pos.R
import kotlinx.android.synthetic.main.fragment_settings_updated.*

class SettingsUpdatedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings_updated, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsUpdatedAnimationView.playAnimation()

        settingsUpdatedFinishButton.setOnClickListener {
            val action = SettingsUpdatedFragmentDirections.actionNavigationSettingsUpdatedToNavigationSettings()
            findNavController().navigate(action)
        }
    }
}
