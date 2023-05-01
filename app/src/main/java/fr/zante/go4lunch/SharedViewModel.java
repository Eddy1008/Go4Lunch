package fr.zante.go4lunch;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;

public class SharedViewModel extends ViewModel {

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
