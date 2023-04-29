package fr.zante.go4lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private static SharedViewModel mySharedViewModel;

    private MutableLiveData<String> myLocation = new MutableLiveData<>();

    public void setMyLocation(String newLocationValue) {
        myLocation.setValue(newLocationValue);
    }

    public LiveData<String> getMyLocation() {
        return this.myLocation;
    }

}
