package fr.zante.go4lunch.ui.restaurantdetail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;

public class RestaurantDetailViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;
    private final GooglePlacesRepository googlePlaceRepository;

    // DATA
    private LiveData<Member> activeMember;
    private LiveData<List<String>> memberLikedRestaurantData;
    private LiveData<List<SelectedRestaurant>> selectedRestaurantsData;
    private LiveData<List<Member>> selectedRestaurantMembersData;
    private LiveData<RestaurantJson> selectedRestaurant;

    // Constructor
    public RestaurantDetailViewModel(MembersRepository repository, GooglePlacesRepository googlePlaceRepository) {
        this.repository = repository;
        this.googlePlaceRepository = googlePlaceRepository;
    }

    // *******************************
    // *********** MEMBERS ***********
    // *******************************

    public void initActiveMember(String memberName) {
        if (this.activeMember != null) {
            return;
        }
        activeMember = repository.getActiveMember(memberName);
    }
    public LiveData<Member> getActiveMember() { return this.activeMember; }

    public void updateMember(Member member) { repository.updateMember(member); }


    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************

    public void addLikedRestaurant(Member activeMember, String restaurantId) { repository.addLikedRestaurant(activeMember, restaurantId); }

    public void initMemberLikedRestaurantsList(Member activeMember) {
        if (this.memberLikedRestaurantData != null) {
            return;
        }
        memberLikedRestaurantData = repository.getActiveMemberLikedRestaurantLiveDataList(activeMember);
    }
    public LiveData<List<String>> getMemberLikedRestaurants() { return this.memberLikedRestaurantData; }

    public void deleteLikedRestaurant(Member activeMember, String restaurantId) { repository.deleteLikedRestaurant(activeMember, restaurantId);}


    // **********************************
    // *********** RESTAURANT ***********
    // **********************************

    public void initSelectedRestaurantById(String myPlaceId) {
        selectedRestaurant = googlePlaceRepository.getRestaurantLiveDataById(myPlaceId);
    }
    public LiveData<RestaurantJson> getRestaurantById() { return this.selectedRestaurant; }


    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************

    public void addSelectedRestaurant(SelectedRestaurant selectedRestaurant) { repository.addSelectedRestaurant(selectedRestaurant);}

    public void initSelectedRestaurantsList() {
        if (this.selectedRestaurantsData != null) {
            return;
        }
        selectedRestaurantsData = repository.getSelectedRestaurantsLiveDataList();
    }

    public LiveData<List<SelectedRestaurant>> getSelectedRestaurants() { return this.selectedRestaurantsData; }

    public void deleteSelectedRestaurant(String selectedRestaurantId) { repository.deleteSelectedRestaurant(selectedRestaurantId);}


    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************

    public void addMemberToSelectedRestaurantMemberList(Member activeMember, String selectedRestaurantId) {
        repository.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
    }

    public void  initSelectedRestaurantMembersList(String selectedRestaurantId) {
        if (this.selectedRestaurantMembersData != null) {
            return;
        }
        selectedRestaurantMembersData = repository.getSelectedRestaurantMemberLiveDataList(selectedRestaurantId);
    }

    public LiveData<List<Member>> getSelectedRestaurantMembers() { return this.selectedRestaurantMembersData; }

    public LiveData<List<Member>> getActiveMemberSelectedRestaurantMembersJoiningList(String activeMemberSelectedRestaurantId) {
        LiveData<List<Member>> myList = repository.getSelectedRestaurantMemberLiveDataList(activeMemberSelectedRestaurantId);
        return myList;
    }

    public void deleteMemberToSelectedRestaurantMemberList(Member activeMember, String selectedRestaurantId) {
        repository.deleteMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
    }
}
