package fr.zante.go4lunch.ui.settings;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.work.Data;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.notification.NotificationRepository;

public class SettingsViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;
    private NotificationRepository notificationRepository;
    private LiveData<Member> activeMember;

    public SettingsViewModel(MembersRepository repository) {
        this.repository = repository;
        this.notificationRepository = new NotificationRepository();
    }

    public void initActiveMember(String memberName) {
        if (this.activeMember != null) {
            return;
        }
        activeMember = repository.getActiveMember(memberName);
    }
    public LiveData<Member> getActiveMember() { return this.activeMember; }

    public void updateMemberNotificationsAllowed(Member member) {
        repository.updateNotificationsAllowed(member);
    }

    public void scheduleDailyNotification(Context context, long initialDelayMillis) {
        Log.d("TAG", "SettingsViewModel : scheduleDailyNotification: Appel au repository pour planifier le WORKER ");
        notificationRepository.scheduleDailyNotificationWorker(context, initialDelayMillis);
    }

    public void cancelDailyNotification() {
        Log.d("TAG", "SettingsViewModel : cancelDailyNotification: Appel au repository pour annuler le WORKER ");
        notificationRepository.cancelDailyNotificationWorker();
    }


}
