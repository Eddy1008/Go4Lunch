package fr.zante.go4lunch.ui.settings;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.databinding.ActivitySettingsBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.ViewModelFactory;
import fr.zante.go4lunch.ui.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel settingsViewModel;
    private ActivitySettingsBinding binding;
    private Member activeMember;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        scheduleDailyNotification();
                        activeMember.setNotificationsAllowed(true);
                        settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                        binding.settingsNotificationSwitchButton.setChecked(true);
                    } else {
                        binding.settingsNotificationSwitchButton.setChecked(false);
                    }
                }
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        settingsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(SettingsViewModel.class);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            settingsViewModel.initActiveMember(firebaseUser.getDisplayName());
            settingsViewModel.getActiveMember().observe(this, member -> {
                activeMember = member;
                binding.settingsNotificationSwitchButton.setChecked(activeMember.isNotificationsAllowed());
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
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
                    getNotificationPermission();
                } else {
                    activeMember.setNotificationsAllowed(false);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                    cancelDailyNotification();
                }
            }
        });
    }

    private void getNotificationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    private void scheduleDailyNotification() {
        createNotificationChannel();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 46);
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
            CharSequence channelName = getString(R.string.settings_activity_channel_name);
            String channelDescription = getString(R.string.settings_activity_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("myNotificationChannel", channelName, importance);
            mChannel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }
    }
}
