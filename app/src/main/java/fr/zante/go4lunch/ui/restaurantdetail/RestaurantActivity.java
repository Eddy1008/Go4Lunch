package fr.zante.go4lunch.ui.restaurantdetail;

import static android.Manifest.permission.CALL_PHONE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.BuildConfig;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.databinding.ActivityRestaurantBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;
    private String userName;

    private RestaurantDetailViewModel restaurantDetailViewModel;
    private Member activeMember;
    private List<String> activeMemberLikedRestaurantList;
    private List<SelectedRestaurant> selectedRestaurantList;
    private SelectedRestaurant mySelectedRestaurant;

    private String restaurantId;
    private RecyclerView recyclerView;
    private List<Member> membersJoiningList = new ArrayList<>();

    private ImageView firstStar;
    private ImageView secondStar;
    private ImageView thirdStar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firstStar = binding.restaurantDetailStarOne;
        secondStar = binding.restaurantDetailStarTwo;
        thirdStar = binding.restaurantDetailStarThree;

        getInfoFromIntent();

        restaurantDetailViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantDetailViewModel.class);

        restaurantDetailViewModel.initActiveMember(userName);
        restaurantDetailViewModel.getActiveMember().observe(this, member -> {
            this.activeMember = member;
            if (activeMember != null) {
                restaurantDetailViewModel.initMemberLikedRestaurantsList(activeMember);
            }
        });

        restaurantDetailViewModel.initSelectedRestaurantsList();
        restaurantDetailViewModel.getSelectedRestaurants().observe(this, selectedRestaurants -> {
            this.selectedRestaurantList = new ArrayList<>(selectedRestaurants);
            if (selectedRestaurants != null) {
            }
        });

        restaurantDetailViewModel.initSelectedRestaurantMembersList(restaurantId);
        restaurantDetailViewModel.getSelectedRestaurantMembers().observe(this, members -> {
            membersJoiningList = new ArrayList<>(members);
        });

        setPreviousPageButton();
        getRestaurantDataFromBundle();
        setRecyclerView();
    }

    void getInfoFromIntent() {
        Intent intent = getIntent();
        Bundle myBundle = intent.getBundleExtra("BUNDLE_RESTAURANT_SELECTED");
        restaurantId = (String) myBundle.get("RESTAURANT_PLACE_ID");
        userName = (String) myBundle.get("USER_NAME");
    }

    void getRestaurantDataFromBundle() {
        this.restaurantDetailViewModel.initSelectedRestaurantById(this.restaurantId);
        this.restaurantDetailViewModel.getRestaurantById().observe(this, restaurantJson -> {
            RestaurantJson detailedRestaurant = restaurantJson;
            binding.restaurantDetailName.setText(detailedRestaurant.getName());
            binding.restaurantDetailAddress.setText(detailedRestaurant.getVicinity());
            if (detailedRestaurant.getPhotos() != null) {
                String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
                String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
                String myPhotoURL = myBasePhotoURL + detailedRestaurant.getPhotos().get(0).getPhoto_reference() + apiKey;
                Glide.with(this.getApplicationContext())
                        .load(myPhotoURL)
                        .into(binding.restaurantDetailPhoto);
            }

            float myIntRating = detailedRestaurant.getRating() * 3 / 5;
            if (myIntRating > 2.6) {
                firstStar.setVisibility(View.VISIBLE);
                secondStar.setVisibility(View.VISIBLE);
                thirdStar.setVisibility(View.VISIBLE);
            } else if (myIntRating > 1.8) {
                firstStar.setVisibility(View.VISIBLE);
                secondStar.setVisibility(View.VISIBLE);
                thirdStar.setVisibility(View.INVISIBLE);
            } else if (myIntRating > 1) {
                firstStar.setVisibility(View.VISIBLE);
                secondStar.setVisibility(View.INVISIBLE);
                thirdStar.setVisibility(View.INVISIBLE);
            } else {
                firstStar.setVisibility(View.INVISIBLE);
                secondStar.setVisibility(View.INVISIBLE);
                thirdStar.setVisibility(View.INVISIBLE);
            }

            // Set the selected restaurant fab button
            setSelectedRestaurantButtonColor();
            binding.restaurantDetailFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMemberSelectedRestaurant(detailedRestaurant.getPlace_id(), detailedRestaurant.getName());
                }
            });

            // Set the phone button
            binding.restaurantDetailLinearLayoutPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + detailedRestaurant.getFormatted_phone_number()));
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
            setRestaurantLikedButtonColor();
            binding.restaurantDetailLinearLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateLikedRestaurantList();
                }
            });

            // Set the Website button
            if (detailedRestaurant.getWebsite() != null) {
                binding.restaurantDetailLinearLayoutWebsite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intentWebsite = new Intent(Intent.ACTION_VIEW);
                        intentWebsite.setData(Uri.parse(detailedRestaurant.getWebsite()));
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
        });
    }

    void setRestaurantLikedButtonColor() {
        restaurantDetailViewModel.getMemberLikedRestaurants().observe(this, strings -> {
            this.activeMemberLikedRestaurantList = new ArrayList<>(strings);
            if (activeMemberLikedRestaurantList.contains(restaurantId)) {
                binding.restaurantDetailImageviewLike.setImageResource(R.drawable.ic_baseline_is_like_star_24);
            } else {
                binding.restaurantDetailImageviewLike.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
    }
    
    void updateLikedRestaurantList() {
        if (activeMemberLikedRestaurantList.contains(restaurantId)) {
            restaurantDetailViewModel.deleteLikedRestaurant(activeMember, restaurantId);
        } else {
            restaurantDetailViewModel.addLikedRestaurant(activeMember, restaurantId);
        }
        restaurantDetailViewModel.initMemberLikedRestaurantsList(activeMember);
    }

    void setSelectedRestaurantButtonColor() {
        restaurantDetailViewModel.getActiveMember().observe(this, member -> {
            activeMember = member;
            if (activeMember.getSelectedRestaurantId().equals(restaurantId)) {
                binding.restaurantDetailFab.setImageResource(R.drawable.ic_baseline_check_circle_24);
            } else {
                binding.restaurantDetailFab.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
            }
        });
    }

    void updateMemberSelectedRestaurant(String selectedRestaurantId, String selectedRestaurantName) {
        if (activeMember.getSelectedRestaurantId().equals("")) {
            activeMember.setSelectedRestaurantId(selectedRestaurantId);
            activeMember.setSelectedRestaurantName(selectedRestaurantName);
            restaurantDetailViewModel.updateMember(activeMember);
            boolean isInSelectedList = false;
            for (int i=0; i<selectedRestaurantList.size(); i++) {
                if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                    isInSelectedList = true;
                }
            }
            if (!isInSelectedList) {
                mySelectedRestaurant =  new SelectedRestaurant(selectedRestaurantId, selectedRestaurantName, 1);
                restaurantDetailViewModel.addSelectedRestaurant(mySelectedRestaurant);
                restaurantDetailViewModel.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
            } else {
                for (int i=0; i<selectedRestaurantList.size(); i++) {
                    if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                        mySelectedRestaurant = selectedRestaurantList.get(i);
                        break;
                    }
                }
                mySelectedRestaurant.setMemberJoiningNumber(mySelectedRestaurant.getMemberJoiningNumber() + 1);
                restaurantDetailViewModel.updateSelectedRestaurant(mySelectedRestaurant);
                restaurantDetailViewModel.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
            }
        } else {
            if (activeMember.getSelectedRestaurantId().equals(selectedRestaurantId)) {
                activeMember.setSelectedRestaurantId("");
                activeMember.setSelectedRestaurantName("");
                restaurantDetailViewModel.updateMember(activeMember);
                for (int i=0; i<selectedRestaurantList.size(); i++) {
                    if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                        mySelectedRestaurant = selectedRestaurantList.get(i);
                        break;
                    }
                }
                mySelectedRestaurant.setMemberJoiningNumber(mySelectedRestaurant.getMemberJoiningNumber() - 1);
                restaurantDetailViewModel.updateSelectedRestaurant(mySelectedRestaurant);
                restaurantDetailViewModel.deleteMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
                if (mySelectedRestaurant.getMemberJoiningNumber() == 0) {
                    restaurantDetailViewModel.deleteSelectedRestaurant(selectedRestaurantId);
                }
            } else {
                for (int i=0; i<selectedRestaurantList.size(); i++) {
                    if (selectedRestaurantList.get(i).getRestaurantId().equals(activeMember.getSelectedRestaurantId())) {
                        mySelectedRestaurant = selectedRestaurantList.get(i);
                        break;
                    }
                }
                mySelectedRestaurant.setMemberJoiningNumber(mySelectedRestaurant.getMemberJoiningNumber() - 1);
                restaurantDetailViewModel.updateSelectedRestaurant(mySelectedRestaurant);
                restaurantDetailViewModel.deleteMemberToSelectedRestaurantMemberList(activeMember, activeMember.getSelectedRestaurantId());
                if (mySelectedRestaurant.getMemberJoiningNumber() == 0) {
                    restaurantDetailViewModel.deleteSelectedRestaurant(activeMember.getSelectedRestaurantId());
                }

                activeMember.setSelectedRestaurantId(selectedRestaurantId);
                activeMember.setSelectedRestaurantName(selectedRestaurantName);
                restaurantDetailViewModel.updateMember(activeMember);
                boolean isInSelectedList = false;
                for (int i=0; i<selectedRestaurantList.size(); i++) {
                    if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                        isInSelectedList = true;
                        mySelectedRestaurant = selectedRestaurantList.get(i);
                        break;
                    }
                }
                if (!isInSelectedList) {
                    mySelectedRestaurant = new SelectedRestaurant(selectedRestaurantId, selectedRestaurantName, 0);
                    restaurantDetailViewModel.addSelectedRestaurant(mySelectedRestaurant);
                }
                mySelectedRestaurant.setMemberJoiningNumber(mySelectedRestaurant.getMemberJoiningNumber() + 1);
                restaurantDetailViewModel.updateSelectedRestaurant(mySelectedRestaurant);
                restaurantDetailViewModel.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
            }
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
        restaurantDetailViewModel.getSelectedRestaurantMembers().observe(this, members -> {
            membersJoiningList = new ArrayList<>(members);
            recyclerView.setAdapter(new RestaurantDetailRecyclerViewAdapter(membersJoiningList));
        });
    }
}
