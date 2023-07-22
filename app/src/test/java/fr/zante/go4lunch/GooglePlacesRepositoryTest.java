package fr.zante.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.data.GooglePlacesApi;
import fr.zante.go4lunch.data.GooglePlacesRepository;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.RestaurantResultById;
import fr.zante.go4lunch.model.RestaurantsResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class GooglePlacesRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private GooglePlacesApi googlePlacesAPi;

    @Mock
    private Observer<List<RestaurantJson>> observer;

    @Mock
    private Observer<RestaurantJson> observerRestaurantJson;

    @Captor
    private ArgumentCaptor<Callback<RestaurantsResult>> callbackCaptor;

    @Captor
    private ArgumentCaptor<Callback<RestaurantResultById>> callbackCaptorById;

    private GooglePlacesRepository googlePlacesRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        googlePlacesRepository = new GooglePlacesRepository(googlePlacesAPi);
    }

    @Test
    public void getRestaurantLiveData_success() {
        // Mock response
        RestaurantsResult restaurantsResult = new RestaurantsResult();
        List<RestaurantJson> restaurantList = new ArrayList<>();
        // Ajoutez des restaurants à la liste ici
        restaurantsResult.setRestaurants(restaurantList);
        Response<RestaurantsResult> response = Response.success(restaurantsResult);

        // Configure le comportement du googlePlacesAPi mock
        Call<RestaurantsResult> call = mock(Call.class);
        when(googlePlacesAPi.getNearbyPlaces(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(call);
        doAnswer(invocation -> {
            Callback<RestaurantsResult> callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(callbackCaptor.capture());

        // Observer le LiveData
        googlePlacesRepository.getRestaurantLiveData(0.0, 0.0).observeForever(observer);

        // Vérifiez que le callback onResponse a été appelé
        verify(call).enqueue(callbackCaptor.getValue());

        // Vérifiez que la liste de restaurants est mise à jour dans le LiveData
        assertEquals(restaurantList, googlePlacesRepository.getRestaurantLiveData(0.0, 0.0).getValue());

        // Vérifiez que l'observer a été notifié
        verify(observer).onChanged(restaurantList);
    }

    @Test
    public void getRestaurantLiveData_failure() {
        // Configure le comportement du googlePlacesAPi mock
        Call<RestaurantsResult> call = mock(Call.class);
        when(googlePlacesAPi.getNearbyPlaces(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(call);
        doAnswer(invocation -> {
            Callback<RestaurantsResult> callback = invocation.getArgument(0);
            callback.onFailure(call, new Throwable());
            return null;
        }).when(call).enqueue(callbackCaptor.capture());

        // Observer le LiveData
        googlePlacesRepository.getRestaurantLiveData(0.0, 0.0).observeForever(observer);

        // Vérifiez que le callback onFailure a été appelé
        verify(call).enqueue(callbackCaptor.getValue());

        // Vérifiez que la liste de restaurants est null dans le LiveData
        assertEquals(null, googlePlacesRepository.getRestaurantLiveData(0.0, 0.0).getValue());

        // Vérifiez que l'observer a été notifié
        verify(observer).onChanged(null);
    }

    @Test
    public void getRestaurantLiveDataById_success() {
        // Mock response
        RestaurantResultById restaurantResultById = new RestaurantResultById();
        RestaurantJson restaurantJson = new RestaurantJson();
        // Initialisez les détails du restaurant ici
        restaurantResultById.setResult(restaurantJson);
        Response<RestaurantResultById> response = Response.success(restaurantResultById);

        // Configure le comportement du googlePlacesAPi mock
        Call<RestaurantResultById> call = mock(Call.class);
        when(googlePlacesAPi.getPlaceInfoById(anyString(), anyString(), anyString())).thenReturn(call);
        doAnswer(invocation -> {
            Callback<RestaurantResultById> callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(callbackCaptorById.capture());

        // Observer le LiveData
        googlePlacesRepository.getRestaurantLiveDataById("myPlaceId").observeForever(observerRestaurantJson);

        // Vérifiez que le callback onResponse a été appelé
        verify(call).enqueue(callbackCaptorById.getValue());

        // Vérifiez que le restaurant est mis à jour dans le LiveData
        assertEquals(restaurantJson, googlePlacesRepository.getRestaurantLiveDataById("myPlaceId").getValue());

        // Vérifiez que l'observer a été notifié
        verify(observerRestaurantJson).onChanged(restaurantJson);
    }

    @Test
    public void getRestaurantLiveDataById_failure() {
        // Configure le comportement du googlePlacesAPi mock
        Call<RestaurantResultById> call = mock(Call.class);
        when(googlePlacesAPi.getPlaceInfoById(anyString(), anyString(), anyString())).thenReturn(call);
        doAnswer(invocation -> {
            Callback<RestaurantResultById> callback = invocation.getArgument(0);
            callback.onFailure(call, new Throwable());
            return null;
        }).when(call).enqueue(callbackCaptorById.capture());

        // Observer le LiveData
        googlePlacesRepository.getRestaurantLiveDataById("myPlaceId").observeForever(observerRestaurantJson);

        // Vérifiez que le callback onFailure a été appelé
        verify(call).enqueue(callbackCaptorById.getValue());

        // Vérifiez que le restaurant est null dans le LiveData
        assertEquals(null, googlePlacesRepository.getRestaurantLiveDataById("myPlaceId").getValue());

        // Vérifiez que l'observer a été notifié
        verify(observerRestaurantJson).onChanged(null);
    }
}
