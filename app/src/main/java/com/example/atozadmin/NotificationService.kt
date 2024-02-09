package com.example.atozadmin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val channelId="AtoZStoreAdmin"
        val channel=NotificationChannel(channelId,"AtoZStore", NotificationManager.IMPORTANCE_HIGH).apply {
            description="AtoZStore"
            enableLights(true)
        }
        val manager=getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val pendingIntent=PendingIntent.getActivity(this,0, Intent(this, AdminMainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT)
        val notification=NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(),notification)
    }

}