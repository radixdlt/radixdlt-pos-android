package com.radixdlt.android.apps.pos.identity

import com.radixdlt.client.application.RadixApplicationAPI
import com.radixdlt.client.application.identity.RadixIdentities
import com.radixdlt.client.application.identity.RadixIdentity
import com.radixdlt.client.core.BootstrapConfig
import kotlin.reflect.KProperty

object RadixApplicationAPI {
    private lateinit var radixApplicationAPI: RadixApplicationAPI
    private val radixIdentity: RadixIdentity = RadixIdentities.createNew()

    fun init(bootstrapConfig: BootstrapConfig) {
        this.radixApplicationAPI = RadixApplicationAPI.create(bootstrapConfig, radixIdentity)
        radixApplicationAPI.pull()
    }

    operator fun getValue(thisRef: Any?, prop: KProperty<*>): RadixApplicationAPI {
        return radixApplicationAPI
    }

    private operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: RadixIdentity) {}
}
