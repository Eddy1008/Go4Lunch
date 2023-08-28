package fr.zante.go4lunch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.login.LoginViewModel;

public class LoginViewModelTest {

    private LoginViewModel loginViewModel;
    private MembersRepository membersRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        membersRepository = mock(MembersRepository.class);

        loginViewModel = new LoginViewModel(membersRepository);
    }

    @Test
    public void testAddMember() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        loginViewModel.addMember(member);
        verify(membersRepository).addMember(member);
    }
}
