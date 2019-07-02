package com.radixdlt.android.apps.pos.util

import androidx.annotation.IntDef
import androidx.annotation.StringDef

const val TOKEN_REFERENCE_ADDRESS = "9gktrzDLiwxDXSxcpV9PQEkBr59g5vrvRhoXC4YPBXnTi5hsRzg"
const val TOKEN_REFERENCE_SYMBOL = "USD"

const val ZERO = "0"

@Retention(AnnotationRetention.SOURCE)
@IntDef(PAYMENT_ADDRESS, INVOICE_NAME, MASTER_PIN)
annotation class Settings

const val PAYMENT_ADDRESS = 0
const val INVOICE_NAME = 1
const val MASTER_PIN = 2

@Retention(AnnotationRetention.SOURCE)
@StringDef(PIN_ERROR_TWO_ATTEMPTS_REMAINING, PIN_ERROR_ONE_ATTEMPT_REMAINING, CARD_BLOCKED, SELECT_OK)
annotation class CardStatusCode

const val SELECT_OK = "9000"
const val PIN_ERROR_TWO_ATTEMPTS_REMAINING = "9a04"
const val PIN_ERROR_ONE_ATTEMPT_REMAINING = "9904"
const val CARD_BLOCKED = "9f04"

@Retention(AnnotationRetention.SOURCE)
@IntDef(PIN_ERROR_FIRST_ATTEMPT, PIN_ERROR_SECOND_ATTEMPT)
annotation class CardPinError

const val PIN_ERROR_FIRST_ATTEMPT = 0
const val PIN_ERROR_SECOND_ATTEMPT = 1

@Retention(AnnotationRetention.SOURCE)
@IntDef(SETTINGS_UNLOCK, SETTINGS_UPDATE, SETTINGS_UPDATE_PAYMENT_ADDRESS, SETTINGS_UPDATE_INVOICE_NAME)
annotation class SettingsPinAction

const val SETTINGS_UNLOCK = 0
const val SETTINGS_UPDATE = 1
const val SETTINGS_UPDATE_PAYMENT_ADDRESS = 2
const val SETTINGS_UPDATE_INVOICE_NAME = 3
