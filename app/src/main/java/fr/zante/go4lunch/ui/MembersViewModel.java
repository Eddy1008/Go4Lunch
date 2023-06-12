package fr.zante.go4lunch.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;

public class MembersViewModel extends ViewModel {

    // Repository
    private final MembersRepository repository;
    private final GooglePlacesRepository googlePlaceRepository;

    // DATA
    private LiveData<List<Member>> membersData;
    private LiveData<List<RestaurantJson>> restaurantsData;
    private LiveData<Member> activeMember;
    private LiveData<List<SelectedRestaurant>> selectedRestaurantsData;

    // Constructor
    public MembersViewModel(MembersRepository repository, GooglePlacesRepository googlePlaceRepository) {
        this.repository = repository;
        this.googlePlaceRepository = googlePlaceRepository;
    }

    // *******************************
    // *********** MEMBERS ***********
    // *******************************

    public void initMembersList() {
        if (this.membersData != null) {
            return;
        }
        membersData = repository.getMembersLiveDataList();
    }
    public LiveData<List<Member>> getMembers() { return this.membersData; }

    public void initActiveMember(String memberName) {
        if (this.activeMember != null) {
            return;
        }
        activeMember = repository.getActiveMember(memberName);
    }
    public LiveData<Member> getActiveMember() { return this.activeMember; }


    // ***********************************
    // *********** RESTAURANTS ***********
    // ***********************************

    public void init(double lat, double lng) {
        if (this.restaurantsData != null) {
            return;
        }
        restaurantsData = googlePlaceRepository.getRestaurantLiveData(lat, lng);
    }

    public LiveData<List<RestaurantJson>> getRestaurants() {
        return this.restaurantsData;
    }


    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************

    public void initSelectedRestaurantsList() {
        if (this.selectedRestaurantsData != null) {
            return;
        }
        selectedRestaurantsData = repository.getSelectedRestaurantsLiveDataList();
    }

    public LiveData<List<SelectedRestaurant>> getSelectedRestaurants() { return this.selectedRestaurantsData; }

    // **************************
    // ********* SHARED *********
    // **************************

    // User Name
    // Set in MainActivity
    // For use in ListviewFragment for sending to RestaurantActivity
    private MutableLiveData<String> myUserName = new MutableLiveData<>();
    public void setMyUserName(String userName) { myUserName.setValue(userName); }
    public String getMyUserName() { return myUserName.getValue(); }

    // User Position
    // Set in MapviewFragment
    // For use in ListviewFragment
    private MutableLiveData<LatLng> myLatLng = new MutableLiveData<>();
    public void setMyLatLng(LatLng myNewLatLng) {
        myLatLng.setValue(myNewLatLng);
    }
    public double getMyLat() {
        return myLatLng.getValue().latitude;
    }
    public double getMyLng() {
        return myLatLng.getValue().longitude;
    }

}
