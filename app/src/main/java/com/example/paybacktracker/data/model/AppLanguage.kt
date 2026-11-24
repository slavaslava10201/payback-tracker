package com.example.paybacktracker.data.model

enum class AppLanguage(val code: String, val title: String, val nativeName: String) {
    RU("ru", "Русский", "Русский"),
    EN("en", "English", "English"),
    UK("uk", "Украинский", "Українська"),
    BE("be", "Белорусский", "Беларуская"),
    KK("kk", "Казахский", "Қазақша");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: RU
        }
    }
}
