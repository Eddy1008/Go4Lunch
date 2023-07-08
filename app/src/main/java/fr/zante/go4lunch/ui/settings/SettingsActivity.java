package fr.zante.go4lunch.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import fr.zante.go4lunch.databinding.ActivitySettingsBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.notification.NotificationWorkManager;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel;
    private ActivitySettingsBinding binding;
    private Member activeMember;
    private FirebaseAuth firebaseAuth;
    private WorkRequest uploadWorkRequest;
    private WorkManager workManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(SettingsViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            settingsViewModel.initActiveMember(firebaseUser.getDisplayName());
            settingsViewModel.getActiveMember().observe(this, member -> {
                activeMember = member;
                if (activeMember.isNotificationsAllowed()) {
                    Log.d("TAG", "SETTINGS_ACTIVITY : onCreate: activeMember = " + activeMember.getName()
                            + " \n activeMember.isNotificationsAllowed() = " + activeMember.isNotificationsAllowed());
                    binding.settingsNotificationSwitchButton.setChecked(true);
                    //scheduleDailyNotification();
                } else {
                    Log.d("TAG", "SETTINGS_ACTIVITY : onCreate: activeMember = " + activeMember.getName()
                            + " \n activeMember.isNotificationsAllowed() = " + activeMember.isNotificationsAllowed());
                    binding.settingsNotificationSwitchButton.setChecked(false);
                    //cancelDailyNotification();
                }
            });
        }

        /**
        // do one time :
        //uploadWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorkManager.class).build();
        // repeat each 10 seconds:
        uploadWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 10000, TimeUnit.MILLISECONDS).build();
        // repeat each day:
        //uploadWorkRequest = new PeriodicWorkRequest.Builder(NotificationWorkManager.class, 1, TimeUnit.DAYS).build();
        WorkManager.getInstance(SettingsActivity.this).enqueue(uploadWorkRequest);
         */

        setPreviousPageButton();
        setSwitchButton();

    }

    private void setPreviousPageButton() {
        binding.settingsPreviousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setSwitchButton() {
        binding.settingsNotificationSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("TAG", "SETTINGS_ACTIVITY : setSwitchButton() : onCheckedChanged: TRUE ");
                    activeMember.setNotificationsAllowed(true);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                    Log.d("TAG", "SETTINGS_ACTIVITY : setSwitchButton() : activeMember.isNotificationsAllowed() = " + activeMember.isNotificationsAllowed());

                    scheduleDailyNotification();
                    binding.settingsTextviewTitle.setText("Notification Work Manager enabled");
                } else {
                    Log.d("TAG", "SETTINGS_ACTIVITY : setSwitchButton() : onCheckedChanged: FALSE ");
                    activeMember.setNotificationsAllowed(false);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                    Log.d("TAG", "SETTINGS_ACTIVITY : setSwitchButton() : activeMember.isNotificationsAllowed() = " + activeMember.isNotificationsAllowed());

                    cancelDailyNotification();
                    binding.settingsTextviewTitle.setText("Notification Work Manager disabled");
                }
            }
        });
    }

    private void scheduleDailyNotification() {
        createNotificationChannel();

        // Définir l'heure souhaitée (12h00)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 1);
        long initialDelayMillis = calculateInitialDelay(calendar);

        Log.d("TAG", "SETTINGS_ACTIVITY : scheduleDailyNotification: appel au ViewModel pour planifier le WORKER: ");
        settingsViewModel.scheduleDailyNotification(this, initialDelayMillis);
    }

    private void cancelDailyNotification() {
        Log.d("TAG", "SETTINGS_ACTIVITY : cancelDailyNotification: appel au ViewModel pour annuler le WORKER: ");
        settingsViewModel.cancelDailyNotification();
    }

    private long calculateInitialDelay(Calendar sheduledTime) {
        Calendar currentTime = Calendar.getInstance();
        long timeDiff = sheduledTime.getTimeInMillis() - currentTime.getTimeInMillis();

        if (timeDiff <= 0) {
            sheduledTime.add(Calendar.DAY_OF_MONTH, 1);
            timeDiff = sheduledTime.getTimeInMillis() - currentTime.getTimeInMillis();
        }

        return timeDiff;
    }

    private void createNotificationChannel() {
        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Messages";
            String channelDescription = "Channel for alarm manager Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("myNotificationChannel", channelName, importance);
            mChannel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
}
