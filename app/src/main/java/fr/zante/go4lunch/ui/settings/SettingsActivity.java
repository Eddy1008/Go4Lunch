package fr.zante.go4lunch.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import fr.zante.go4lunch.databinding.ActivitySettingsBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel;
    private ActivitySettingsBinding binding;
    private Member activeMember;
    private FirebaseAuth firebaseAuth;

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
                    binding.settingsNotificationSwitchButton.setChecked(true);
                } else {
                    binding.settingsNotificationSwitchButton.setChecked(false);
                }
            });
        }

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
                    activeMember.setNotificationsAllowed(true);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                    scheduleDailyNotification();
                } else {
                    activeMember.setNotificationsAllowed(false);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                    cancelDailyNotification();
                }
            }
        });
    }

    private void scheduleDailyNotification() {
        createNotificationChannel();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        long initialDelayMillis = calculateInitialDelay(calendar);

        settingsViewModel.scheduleDailyNotification(this, initialDelayMillis);
    }

    private void cancelDailyNotification() {
        settingsViewModel.cancelDailyNotification();
    }

    private long calculateInitialDelay(Calendar scheduledTime) {
        Calendar currentTime = Calendar.getInstance();
        long timeDiff = scheduledTime.getTimeInMillis() - currentTime.getTimeInMillis();

        if (timeDiff <= 0) {
            scheduledTime.add(Calendar.DAY_OF_MONTH, 1);
            timeDiff = scheduledTime.getTimeInMillis() - currentTime.getTimeInMillis();
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
