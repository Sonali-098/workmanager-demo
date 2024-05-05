package com.spdfs.workmanagerdemo

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    companion object {
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Log.d("doWork", "doWork: Success function called")
        createNotificationChannel()
        showNotification()
        return Result.success()
    }

    //create notification channel and notification manager
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val channelName = "Advik"
            val channelDescription = "It is notification channel"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //create notification channel
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        //explicit intent, pending intent
        //to create a pending intent we need to create an intent.
        //flags are used to place activity in the stack or how the activity is stored in the stack
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //create a notification and set its important properties like icon, title and description text.
        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("New Task")
            .setContentText("Subscribe Channel")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            //notify method is used to trigger the notification
            notify(NOTIFICATION, notification.build())
        }
    }
}