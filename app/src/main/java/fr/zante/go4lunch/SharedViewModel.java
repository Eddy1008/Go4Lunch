package fr.zante.go4lunch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class SharedViewModel extends ViewModel {

    // User Id
    // Set in MainActivity
    // For use in ListviewFragment for sending to RestaurantActivity
    private MutableLiveData<String> myUserId = new MutableLiveData<>();
    public void setMyUserId(String userId) { myUserId.setValue(userId); }
    public String getMyUserId() { return myUserId.getValue(); }

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
