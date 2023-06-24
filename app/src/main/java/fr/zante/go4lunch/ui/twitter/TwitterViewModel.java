package fr.zante.go4lunch.ui.twitter;

import androidx.lifecycle.ViewModel;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;

public class TwitterViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;

    public TwitterViewModel(MembersRepository repository) {
        this.repository = repository;
    }

    public void addMember(Member member) {
        repository.addMember(member);
    }
}
