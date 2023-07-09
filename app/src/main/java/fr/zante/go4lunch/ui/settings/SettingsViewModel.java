package fr.zante.go4lunch.ui.settings;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.notification.NotificationRepository;

public class SettingsViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;
    private NotificationRepository notificationRepository;

    // DATA
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
        notificationRepository.scheduleDailyNotificationWorker(context, initialDelayMillis);
    }

    public void cancelDailyNotification() {
        notificationRepository.cancelDailyNotificationWorker();
    }


}
