package fr.zante.go4lunch;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.MembersViewModel;

public class MembersViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MembersViewModel membersViewModel;
    private MembersRepository membersRepository;
    private GooglePlacesRepository googlePlacesRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        membersRepository = mock(MembersRepository.class);

        googlePlacesRepository = mock(GooglePlacesRepository.class);

        membersViewModel = new MembersViewModel(membersRepository, googlePlacesRepository);
    }

    // *******************************
    // *********** MEMBERS ***********
    // *******************************
    @Test
    public void testInitMembersList() {
        membersViewModel.initMembersList();
        verify(membersRepository).getMembersLiveDataList();
    }

    @Test
    public void testGetMembers() {
        LiveData<List<Member>> memberListLiveData = membersViewModel.getMembers();
        assertSame(membersViewModel.getMembers(), memberListLiveData);
    }

    @Test
    public void testInitActiveMember() {
        membersViewModel.initActiveMember("eddy");
        verify(membersRepository).getActiveMember("eddy");
    }

    @Test
    public void testGetActiveMember() {
        LiveData<Member> memberLiveData = membersViewModel.getActiveMember();
        assertSame(membersViewModel.getActiveMember(), memberLiveData);
    }

    // ***********************************
    // *********** RESTAURANTS ***********
    // ***********************************
    @Test
    public void testInit() {
        membersViewModel.init(0,0);
        verify(googlePlacesRepository).getRestaurantLiveData(0,0);
    }

    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    @Test
    public void testInitSelectedRestaurantsList() {
        membersViewModel.initSelectedRestaurantsList();
        verify(membersRepository).getSelectedRestaurantsLiveDataList();
    }

    @Test
    public void testGetSelectedRestaurants() {
        LiveData<List<SelectedRestaurant>> selectedRestaurantLiveDataList = membersViewModel.getSelectedRestaurants();
        assertSame(membersViewModel.getSelectedRestaurants(), selectedRestaurantLiveDataList);
    }

    // **************************
    // ********* SHARED *********
    // **************************
    @Test
    public void testSetMyUserName() {
        String newUserName = "NewUserName";
        membersViewModel.setMyUserName(newUserName);

        String retrievedUserName = membersViewModel.getMyUserName();

        assert(retrievedUserName.equals(newUserName));
    }

    @Test
    public void testGetMyUserName() {
        String expectedUserName = "ExpectedUserName";
        membersViewModel.setMyUserName(expectedUserName);

        String retrievedUserName = membersViewModel.getMyUserName();

        assert(retrievedUserName.equals(expectedUserName));
    }

    @Test
    public void testSetMyLatLng() {
        LatLng latLng = new LatLng(0,0);
        membersViewModel.setMyLatLng(latLng);

        LatLng retrievedLatLng = new LatLng(membersViewModel.getMyLat(), membersViewModel.getMyLng());
        assert(retrievedLatLng.equals(latLng));
    }
}
