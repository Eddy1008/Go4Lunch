package fr.zante.go4lunch.ui.registration;

import androidx.lifecycle.ViewModel;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;

public class RegisterViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;

    public RegisterViewModel(MembersRepository repository) {
        this.repository = repository;
    }

    public void addMember(Member member) {
        repository.addMember(member);
    }
}
