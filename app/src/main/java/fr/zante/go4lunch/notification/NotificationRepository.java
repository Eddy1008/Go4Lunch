package fr.zante.go4lunch.notification;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class NotificationRepository {

    private static final String TAG_DAILY_NOTIFICATION_WORKER = "daily_notification_worker";

    public void scheduleDailyNotificationWorker(Context context, long initialDelayMillis) {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
                .addTag(TAG_DAILY_NOTIFICATION_WORKER)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("periodicWorkRequest", ExistingPeriodicWorkPolicy.KEEP, periodicWorkRequest);
    }

    public void cancelDailyNotificationWorker() {
        WorkManager.getInstance().cancelAllWorkByTag(TAG_DAILY_NOTIFICATION_WORKER);
    }
}
