package fr.zante.go4lunch.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;

public class SettingsViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;

    private LiveData<Member> activeMember;

    public SettingsViewModel(MembersRepository repository) {
        this.repository = repository;
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
}
