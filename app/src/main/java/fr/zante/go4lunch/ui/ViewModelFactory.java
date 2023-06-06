package fr.zante.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.data.RetrofitService;

public class ViewModelFactory implements ViewModelProvider.Factory{

    private static ViewModelFactory factory;

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

    private final MembersRepository membersRepository = new MembersRepository();

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantsViewModel.class)) {
            return (T) new RestaurantsViewModel(googlePlacesRepository);
        }
        if (modelClass.isAssignableFrom(MembersViewModel.class)) {
            return (T) new MembersViewModel(membersRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
