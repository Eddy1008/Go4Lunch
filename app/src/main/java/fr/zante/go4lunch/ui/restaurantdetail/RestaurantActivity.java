package fr.zante.go4lunch.ui.restaurantdetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
