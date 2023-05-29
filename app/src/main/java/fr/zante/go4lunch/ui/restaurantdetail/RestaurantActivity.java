package fr.zante.go4lunch.ui.restaurantdetail;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.SharedViewModel;
import fr.zante.go4lunch.data.MemberRepository;
import fr.zante.go4lunch.databinding.ActivityRestaurantBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.ui.RestaurantsViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;
    private RestaurantsViewModel restaurantsViewModel;
    private String userId;
    private MemberRepository repository;
    private boolean isThisRestaurantLiked;
    private boolean isThisRestaurantSelected;
    private String restaurantId;
    private String restaurantName;
    private RecyclerView recyclerView;
    private List<Member> membersJoiningList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getInfoFromIntent();

        repository = MemberRepository.getInstance();
        repository.getSelectedRestaurantMemberList(restaurantId);

        setPreviousPageButton();
        getRestaurantDataFromBundle();
        setRecyclerView();
    }

    void getInfoFromIntent() {
        Intent intent = getIntent();
        Bundle myBundle = intent.getBundleExtra("BUNDLE_RESTAURANT_SELECTED");
        restaurantId = (String) myBundle.get("RESTAURANT_PLACE_ID");
        userId = (String) myBundle.get("USER_ID");
    }

    void getRestaurantDataFromBundle() {
        this.restaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        this.restaurantsViewModel.initSelectedRestaurant(this.restaurantId);
        this.restaurantsViewModel.getRestaurantById().observe(this, restaurantJson -> {

            binding.restaurantDetailName.setText(restaurantJson.getName());
            binding.restaurantDetailAddress.setText(restaurantJson.getVicinity());
            String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
            String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
            String myPhotoURL = myBasePhotoURL + restaurantJson.getPhotos().get(0).getPhoto_reference() + apiKey;

            Glide.with(this.getApplicationContext())
                    .load(myPhotoURL)
                    .into(binding.restaurantDetailPhoto);

            // Set the selected restaurant fab button
            isThisRestaurantSelected = repository.isMySelectedRestaurant(restaurantId);
            setSelectedRestaurantButtonColor(isThisRestaurantSelected);
            binding.restaurantDetailFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repository.updateMemberSelectedRestaurant(restaurantId, restaurantName);
                    isThisRestaurantSelected = !isThisRestaurantSelected;
                    setSelectedRestaurantButtonColor(isThisRestaurantSelected);
                }
            });

            // Set the phone button
            binding.restaurantDetailLinearLayoutPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + restaurantJson.getFormatted_phone_number()));
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(intent);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{CALL_PHONE}, 1);
                        }
                    }
                }
            });

            // Set the like button
            isThisRestaurantLiked = repository.isLikedRestaurant(restaurantJson.getPlace_id());
            setRestaurantLikedButtonColor(isThisRestaurantLiked);
            binding.restaurantDetailLinearLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    repository.updateMemberRestaurantLikedList(restaurantJson.getPlace_id());
                    isThisRestaurantLiked = !isThisRestaurantLiked;
                    setRestaurantLikedButtonColor(isThisRestaurantLiked);
                }
            });

            // Set the Website button
            if (restaurantJson.getWebsite() != null) {
                binding.restaurantDetailLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentWebsite = new Intent(Intent.ACTION_VIEW);
                        intentWebsite.setData(Uri.parse(restaurantJson.getWebsite()));
                        startActivity(intentWebsite);
                    }
                });
            } else {
                binding.restaurantDetailLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(RestaurantActivity.this, getString(R.string.no_website_available), Toast.LENGTH_SHORT).show();
                    }
                });
            }



            // used if selected button clicked
            restaurantName = restaurantJson.getName();
        });
    }

    void setRestaurantLikedButtonColor(boolean isLiked) {
        if (isLiked) {
            binding.restaurantDetailImageviewLike.setImageResource(R.drawable.ic_baseline_is_like_star_24);
        } else {
            binding.restaurantDetailImageviewLike.setImageResource(R.drawable.ic_baseline_star_24);
        }
    }

    void setSelectedRestaurantButtonColor(boolean isSelected) {
        if (isSelected) {
            binding.restaurantDetailFab.setImageResource(R.drawable.ic_baseline_check_circle_24);
        } else {
            binding.restaurantDetailFab.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
        }
    }

    void setPreviousPageButton() {
        binding.restaurantDetailPreviousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void setRecyclerView() {
        recyclerView = binding.restaurantDetailRecyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        // TODO PB asynchrone
        /**
        List<String> membersJoiningIdList = repository.getMySelectedRestaurantMemberList();
        for (int i=0; i<membersJoiningIdList.size(); i++) {
            Member memberJoining = repository.getMemberById(membersJoiningIdList.get(i));
            membersJoiningList.add(memberJoining);
        }
         */
        // TODO temporaire: a retirer !!!
        membersJoiningList = repository.getMembersList();
        recyclerView.setAdapter(new RestaurantDetailRecyclerViewAdapter(membersJoiningList));
    }
}
