package fr.zante.go4lunch.ui.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        // membersViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MembersViewModel.class);
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
                    Toast.makeText(SettingsActivity.this, "Notifications activées", Toast.LENGTH_SHORT).show();
                    activeMember.setNotificationsAllowed(true);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                } else {
                    Toast.makeText(SettingsActivity.this, "Notifications desactivées", Toast.LENGTH_SHORT).show();
                    activeMember.setNotificationsAllowed(false);
                    settingsViewModel.updateMemberNotificationsAllowed(activeMember);
                }
            }
        });
    }
}
