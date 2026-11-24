package com.example.paybacktracker.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    private val symbols = DecimalFormatSymbols(Locale("ru", "RU")).apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }

    private val integerFormat = DecimalFormat("#,##0", symbols)
    private val decimalFormat = DecimalFormat("#,##0.00", symbols)

    fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            "${integerFormat.format(amount)} ₽"
        } else {
            "${decimalFormat.format(amount)} ₽"
        }
    }

    fun formatBalance(balance: Double): String {
        val absBalance = kotlin.math.abs(balance)
        val formatted = if (absBalance % 1.0 == 0.0) {
            integerFormat.format(absBalance)
        } else {
            decimalFormat.format(absBalance)
        }

        return when {
            balance > 0 -> "+$formatted ₽"
            balance < 0 -> "-$formatted ₽"
            else -> "0 ₽"
        }
    }
}
