package com.example.familyflow

import android.app.Application
import com.example.familyflow.api.SessionManager

class FamilyFlowApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize SessionManager when the app starts
        SessionManager.init(this)
    }
}