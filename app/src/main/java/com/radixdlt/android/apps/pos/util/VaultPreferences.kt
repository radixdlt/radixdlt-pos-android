package com.radixdlt.android.apps.pos.util

object VaultPreferences {
    private const val VAULT_PIN = "pin"
    private const val VAULT_PAYMENT_ADDRESS = "payment_address"
    private const val VAULT_INVOICE_NAME = "invoice_name"

    fun setVaultPaymentAddress(paymentAddress: String) {
        Vault.getVault().edit().putString(VAULT_PAYMENT_ADDRESS, paymentAddress).apply()
    }

    fun getVaultPaymentAddress(): String {
        return Vault.getVault().getString(VAULT_PAYMENT_ADDRESS, null) ?: ""
    }

    fun setVaultInvoiceName(paymentAddress: String) {
        Vault.getVault().edit().putString(VAULT_INVOICE_NAME, paymentAddress).apply()
    }

    fun getVaultInvoiceName(): String {
        return Vault.getVault().getString(VAULT_INVOICE_NAME, null) ?: ""
    }

    fun setVaultPIN(key: String) {
        Vault.getVault().edit().putString(VAULT_PIN, key).apply()
    }

    fun getVaultPIN(): String {
        return Vault.getVault().getString(VAULT_PIN, null) ?: ""
    }
}
