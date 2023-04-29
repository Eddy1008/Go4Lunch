package fr.zante.go4lunch.ui.restaurantdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.databinding.ActivityRestaurantBinding;
import fr.zante.go4lunch.model.RestaurantJson;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;
    private RestaurantJson restaurant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setPreviousPageButton();

        getRestaurantDataFromBundle();
    }

    void getRestaurantDataFromBundle() {
        Intent intent = getIntent();
        Bundle myBundle = intent.getBundleExtra("BUNDLE_RESTAURANT_CLICKED");
        restaurant = (RestaurantJson) myBundle.get("RESTAURANT_OBJECT");

        binding.restaurantDetailName.setText(restaurant.getName());
        binding.restaurantDetailAddress.setText(restaurant.getVicinity());


        String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
        String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
        String myPhotoURL = myBasePhotoURL + restaurant.getPhotos().get(0).getPhoto_reference() + apiKey;

        Glide.with(this.getApplicationContext())
                .load(myPhotoURL)
                .into(binding.restaurantDetailPhoto);
    }

    void setPreviousPageButton() {
        binding.restaurantDetailPreviousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
