package fr.zante.go4lunch;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantDetailViewModel;

public class RestaurantDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private RestaurantDetailViewModel restaurantDetailViewModel;
    private MembersRepository membersRepository;
    private GooglePlacesRepository googlePlacesRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        membersRepository = mock(MembersRepository.class);

        googlePlacesRepository = mock(GooglePlacesRepository.class);

        restaurantDetailViewModel = new RestaurantDetailViewModel(membersRepository, googlePlacesRepository);
    }

    // *******************************
    // *********** MEMBERS ***********
    // *******************************
    @Test
    public void testInitActiveMember() {
        restaurantDetailViewModel.initActiveMember("eddy");
        verify(membersRepository).getActiveMember("eddy");
    }

    @Test
    public void testGetActiveMember() {
        LiveData<Member> memberLiveData = restaurantDetailViewModel.getActiveMember();
        assertSame(restaurantDetailViewModel.getActiveMember(), memberLiveData);
    }

    @Test
    public void testUpdateMember() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        restaurantDetailViewModel.updateMember(member);
        verify(membersRepository).updateMember(member);
    }

    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************
    @Test
    public void testAddLikedRestaurant() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        String restId = "123";
        restaurantDetailViewModel.addLikedRestaurant(member, restId);
        verify(membersRepository).addLikedRestaurant(member, restId);
    }

    @Test
    public void testInitMemberLikedRestaurantsList() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        restaurantDetailViewModel.initMemberLikedRestaurantsList(member);
        verify(membersRepository).getActiveMemberLikedRestaurantLiveDataList(member);
    }

    @Test
    public void testGetMemberLikedRestaurants() {
        LiveData<List<String>> likedRestaurantLiveDataList = restaurantDetailViewModel.getMemberLikedRestaurants();
        assertSame(restaurantDetailViewModel.getMemberLikedRestaurants(), likedRestaurantLiveDataList);
    }

    @Test
    public void testDeleteLikedRestaurant() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        String restId = "123";
        restaurantDetailViewModel.deleteLikedRestaurant(member, restId);
        verify(membersRepository).deleteLikedRestaurant(member, restId);
    }

    // **********************************
    // *********** RESTAURANT ***********
    // **********************************
    @Test
    public void testInitSelectedRestaurantById() {
        restaurantDetailViewModel.initSelectedRestaurantById("eddy");
        verify(googlePlacesRepository).getRestaurantLiveDataById("eddy");
    }

    @Test
    public void testGetRestaurantById() {
        LiveData<RestaurantJson> restaurantLiveData = restaurantDetailViewModel.getRestaurantById();
        assertSame(restaurantDetailViewModel.getRestaurantById(), restaurantLiveData);
    }

    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    @Test
    public void testAddSelectedRestaurant() {
        SelectedRestaurant selectedRestaurant = new SelectedRestaurant("123", "abc", 1);
        restaurantDetailViewModel.addSelectedRestaurant(selectedRestaurant);
        verify(membersRepository).addSelectedRestaurant(selectedRestaurant);
    }

    @Test
    public void testInitSelectedRestaurantsList() {
        restaurantDetailViewModel.initSelectedRestaurantsList();
        verify(membersRepository).getSelectedRestaurantsLiveDataList();
    }

    @Test
    public void testGetSelectedRestaurants() {
        LiveData<List<SelectedRestaurant>> selectedRestaurantLiveDataList = restaurantDetailViewModel.getSelectedRestaurants();
        assertSame(restaurantDetailViewModel.getSelectedRestaurants(), selectedRestaurantLiveDataList);
    }

    @Test
    public void testDeleteSelectedRestaurant() {
        SelectedRestaurant selectedRestaurant = new SelectedRestaurant("123", "abc", 1);
        restaurantDetailViewModel.deleteSelectedRestaurant(selectedRestaurant.getRestaurantId());
        verify(membersRepository).deleteSelectedRestaurant(selectedRestaurant.getRestaurantId());
    }

    @Test
    public void testUpdateSelectedRestaurant() {
        SelectedRestaurant selectedRestaurant = new SelectedRestaurant("123", "abc", 1);
        restaurantDetailViewModel.updateSelectedRestaurant(selectedRestaurant);
        verify(membersRepository).updateSelectedRestaurant(selectedRestaurant);
    }

    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************
    @Test
    public void testAddMemberToSelectedRestaurantMemberList() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        String restId = "123";
        restaurantDetailViewModel.addMemberToSelectedRestaurantMemberList(member, restId);
        verify(membersRepository).addMemberToSelectedRestaurantMemberList(member, restId);
    }

    @Test
    public void testInitSelectedRestaurantMembersList() {
        String restId = "123";
        restaurantDetailViewModel.initSelectedRestaurantMembersList(restId);
        verify(membersRepository).getSelectedRestaurantMemberLiveDataList(restId);
    }

    @Test
    public void testGetSelectedRestaurantMembers() {
        LiveData<List<Member>> selectedRestaurantMemberLiveDataList = restaurantDetailViewModel.getSelectedRestaurantMembers();
        assertSame(restaurantDetailViewModel.getSelectedRestaurantMembers(), selectedRestaurantMemberLiveDataList);
    }

    @Test
    public void testDeleteMemberToSelectedRestaurantMemberList() {
        Member member = new Member(
                "memberId", "name", "mail",
                "avatarUrl", "selectedRestaurantId",
                "selectedRestaurantName", false);
        String restId = "123";
        restaurantDetailViewModel.deleteMemberToSelectedRestaurantMemberList(member, restId);
        verify(membersRepository).deleteMemberToSelectedRestaurantMemberList(member, restId);
    }
}
