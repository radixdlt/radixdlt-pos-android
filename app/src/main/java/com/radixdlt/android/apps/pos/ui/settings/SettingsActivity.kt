package com.radixdlt.android.apps.pos.ui.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.radixdlt.android.apps.pos.R
import kotlinx.android.synthetic.main.tool_bar_blue.*
import net.skoumal.fragmentback.BackFragmentHelper

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initialiseToolBar()
    }

    private fun initialiseToolBar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbarTitleTextView.text = getString(R.string.settings_activity_title_toolbar)

        toolbar.setNavigationOnClickListener {
            val changeMasterPin = findNavController(R.id.nav_host_fragment_settings).currentDestination?.id
            if (changeMasterPin == R.id.navigation_settings_change_master_pin ||
                changeMasterPin == R.id.navigation_settings_change_invoice_name ||
                changeMasterPin == R.id.navigation_settings_change_pay_to_address) {
                findNavController(R.id.nav_host_fragment_settings).popBackStack(R.id.navigation_settings, false)
            } else {
                findNavController(R.id.nav_host_fragment_settings).navigateUp()
            }
        }
    }

    override fun onBackPressed() {
        if (!BackFragmentHelper.fireOnBackPressedEvent(this)) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close_settings -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
