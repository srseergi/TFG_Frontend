package com.sergi.tfg_app.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import java.util.Locale

enum class AppLanguage(val code: String) {
    ENGLISH("en"),
    SPANISH("es"),
    CATALAN("ca");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code == code } ?: ENGLISH
        }
    }
}

object LanguageManager {

    fun setLocale(context: Context, language: AppLanguage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList(Locale.forLanguageTag(language.code))
        }
        // For older versions, the app will restart with the new locale from DataStore
    }

    fun getCurrentLanguage(context: Context): AppLanguage {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
            if (!locales.isEmpty) locales.get(0) else Locale.getDefault()
        } else {
            Locale.getDefault()
        }
        return AppLanguage.fromCode(locale?.language ?: "en")
    }
}
