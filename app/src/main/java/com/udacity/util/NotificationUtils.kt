package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 1

fun NotificationManager.sendNotification(
    message: String,
    context: Context,
    downloadedFile: String,
    status: String
) {
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra("DOWNLOAD_FILE", downloadedFile)
    intent.putExtra("STATUS", status)

    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationBuilder = NotificationCompat.Builder(
        context,
        context.getString(R.string.notification_downloaded_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_assistant_black_24dp, "Check the status", pendingIntent)

    notify(NOTIFICATION_ID, notificationBuilder.build())
}

fun NotificationManager.cancelAllNotifications() {
    cancelAll()
}