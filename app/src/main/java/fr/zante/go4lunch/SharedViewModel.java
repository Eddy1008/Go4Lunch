package fr.zante.go4lunch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class SharedViewModel extends ViewModel {

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
