package com.radixdlt.android.apps.pos

import android.app.Application
import android.util.Base64
import com.radixdlt.android.apps.pos.identity.RadixApplicationAPI
import com.radixdlt.android.apps.pos.util.Vault
import timber.log.Timber

class RadixPOSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RadixApplicationAPI.init()
        Timber.plant(Timber.DebugTree())
        Vault.initialiseVault(this)
        generateEncryptionKey()
    }

    /**
     * Generate encryption key and store it in vault
     */
    private fun generateEncryptionKey() {
        Vault.getVault().apply {
            getString(Vault.ENCRYPTION_KEY_NAME, null) ?: run {
                edit().putString(
                    Vault.ENCRYPTION_KEY_NAME,
                    Base64.encodeToString(Vault.generateKey(), Base64.DEFAULT)
                ).apply()
            }
        }
    }
}
