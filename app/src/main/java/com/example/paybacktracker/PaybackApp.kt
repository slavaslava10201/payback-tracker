package com.example.paybacktracker

import android.app.Application
import com.example.paybacktracker.data.AppDatabase
import com.example.paybacktracker.data.repository.ProjectRepository
import com.example.paybacktracker.data.repository.SettingsRepository

class PaybackApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val repository: ProjectRepository by lazy {
        ProjectRepository(
            projectDao = database.projectDao(),
            transactionDao = database.transactionDao()
        )
    }
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }
}
