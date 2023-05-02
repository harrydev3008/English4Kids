package com.hisu.english4kids.utils.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.hisu.english4kids.R

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = NotificationCompat.Builder(context, context.getString(R.string.notification_learning_channel_id))
            .setAutoCancel(true)
            .setContentTitle(context.getString(R.string.notification_learning_title))
            .setContentText("Vùng lên và học Tiếng Anh đi bạn ơi!")
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_close_circle_fill)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(System.currentTimeMillis())
            .build()

        val notificationMgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationMgr.notify(1, notification)
    }
}