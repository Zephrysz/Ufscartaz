package com.ufscar.ufscartaz

import android.app.Application
import com.ufscar.ufscartaz.data.local.AppDatabase

/**
 * Application class for initialization
 */
class UfscartazApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
    }
} 