package fr.zante.go4lunch.notification;

import android.content.Context;
import android.util.Log;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationRepository {

    private static final String TAG_DAILY_NOTIFICATION_WORKER = "daily_notification_worker";

    public void scheduleDailyNotificationWorker(Context context, long initialDelayMillis) {
        Log.d("TAG", "NOTIFICATION_REPOSITORY : scheduleDailyNotificationWorker: planification du WORKER ");
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 180000, TimeUnit.MILLISECONDS)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .addTag(TAG_DAILY_NOTIFICATION_WORKER)
                .build();

        WorkManager.getInstance(context).enqueue(periodicWorkRequest);
    }

    public void cancelDailyNotificationWorker() {
        Log.d("TAG", "NOTIFICATION_REPOSITORY : cancelDailyNotificationWorker: annulation du WORKER ");
        WorkManager.getInstance().cancelAllWorkByTag(TAG_DAILY_NOTIFICATION_WORKER);
    }
}
