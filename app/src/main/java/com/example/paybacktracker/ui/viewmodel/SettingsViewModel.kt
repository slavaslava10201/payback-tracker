package com.example.paybacktracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.paybacktracker.data.model.AppLanguage
import com.example.paybacktracker.data.model.AppTheme
import com.example.paybacktracker.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme: StateFlow<AppTheme> = settingsRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.SYSTEM
        )

    val language: StateFlow<AppLanguage> = settingsRepository.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.RU
        )

    fun setTheme(newTheme: AppTheme) {
        settingsRepository.setTheme(newTheme)
    }

    fun setLanguage(newLanguage: AppLanguage) {
        settingsRepository.setLanguage(newLanguage)
    }
}
