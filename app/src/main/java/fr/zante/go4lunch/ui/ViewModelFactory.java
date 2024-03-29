package fr.zante.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.FirebaseDatabase;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.data.RetrofitService;
import fr.zante.go4lunch.ui.login.LoginViewModel;
import fr.zante.go4lunch.ui.registration.RegisterViewModel;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantDetailViewModel;
import fr.zante.go4lunch.ui.settings.SettingsViewModel;
import fr.zante.go4lunch.ui.twitter.TwitterViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory{

    private static ViewModelFactory factory;

    // Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static  ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                }
            }
        }
        return factory;
    }

    private final GooglePlacesRepository googlePlacesRepository = new GooglePlacesRepository(
            RetrofitService.getPlacesApi()
    );

    private final MembersRepository membersRepository = MembersRepository.getInstance(database);

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MembersViewModel.class)) {
            return (T) new MembersViewModel(membersRepository, googlePlacesRepository);
        }
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(membersRepository);
        }
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(membersRepository);
        }
        if (modelClass.isAssignableFrom(TwitterViewModel.class)) {
            return (T) new TwitterViewModel(membersRepository);
        }
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(membersRepository);
        }
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(membersRepository,googlePlacesRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
