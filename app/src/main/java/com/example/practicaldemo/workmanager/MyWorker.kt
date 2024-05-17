package com.example.practicaldemo.workmanager

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
import com.example.practicaldemo.MainActivity
import com.example.practicaldemo.R

class MyWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters){

    companion object{
        const val CHANNEL_ID = "channel_id"
        const val NOTIFICATION_ID = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        Log.d("doWork", "Work success")
        showNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        val notification = Notification.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New Task")
            .setContentText("Work Manager Demo")
            .setPriority(Notification.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName=  "channel name"
            val channelDescription = "Channel Description"
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, channelName, channelImportance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(NOTIFICATION_ID, notification.build())
        }
    }
}