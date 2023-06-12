package fr.zante.go4lunch.ui.login;

import androidx.lifecycle.ViewModel;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;

public class LoginViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;

    // Constructor
    public LoginViewModel(MembersRepository repository) {
        this.repository = repository;
    }

    public void addMember(Member member) {
        repository.addMember(member);
    }
}
