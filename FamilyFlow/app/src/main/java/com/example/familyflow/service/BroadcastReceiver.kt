package com.example.familyflow.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.familyflow.util.NetworkUtils
import timber.log.Timber

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val isOnline = NetworkUtils.isNetworkAvailable(context)

            Timber.d("Network status changed. Online: $isOnline")

            if (isOnline) {
                // Start the sync service when connectivity is restored
                val syncIntent = Intent(context, DataSyncService::class.java)
                context.startService(syncIntent)
            }
        }
    }
}