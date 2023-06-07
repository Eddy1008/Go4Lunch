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
import fr.zante.go4lunch.model.SelectedRestaurant;
import fr.zante.go4lunch.ui.MembersViewModel;
import fr.zante.go4lunch.ui.RestaurantsViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;
    private RestaurantsViewModel restaurantsViewModel;
    private String userName;

    private MembersViewModel membersViewModel;
    private Member activeMember;
    private List<String> activeMemberLikedRestaurantList;
    private List<SelectedRestaurant> selectedRestaurantList;

    private String restaurantId;
    private RecyclerView recyclerView;
    private List<Member> membersJoiningList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getInfoFromIntent();

        membersViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MembersViewModel.class);

        membersViewModel.initActiveMember(userName);
        membersViewModel.getActiveMember().observe(this, member -> {
            this.activeMember = member;
            if (activeMember != null) {
                membersViewModel.initMemberLikedRestaurantsList(activeMember);
                Log.d("TAG", "Restaurant Activity : onCreate: \n activeMember Id = " + activeMember.getMemberId() + " \n activeMember Name = " + activeMember.getName());
            }
        });

        membersViewModel.initSelectedRestaurantsList();
        membersViewModel.getSelectedRestaurants().observe(this, selectedRestaurants -> {
            this.selectedRestaurantList = new ArrayList<>(selectedRestaurants);
            if (selectedRestaurants != null) {
                Log.d("TAG", "Restaurant Activity : onCreate: \n selectedRestaurant size = " + selectedRestaurants.size() + " elements");
            }
        });

        membersViewModel.initSelectedRestaurantMembersList(restaurantId);
        membersViewModel.getSelectedRestaurantMembers().observe(this, members -> {
            membersJoiningList = new ArrayList<>(members);
            Log.d("TAG", "Restaurant Activity : onCreate: \n la liste des membres ayant selectionné ce restaurant contient : " + membersJoiningList.size() + " elements");
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
        Log.d("TAG", "getInfoFromIntent: userName = " + userName);
    }

    void getRestaurantDataFromBundle() {
        this.restaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        this.restaurantsViewModel.initSelectedRestaurant(this.restaurantId);
        this.restaurantsViewModel.getRestaurantById().observe(this, restaurantJson -> {

            binding.restaurantDetailName.setText(restaurantJson.getName());
            binding.restaurantDetailAddress.setText(restaurantJson.getVicinity());
            if (restaurantJson.getPhotos() != null) {
                String myBasePhotoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=";
                String apiKey = "&key=" + BuildConfig.MAPS_API_KEY;
                String myPhotoURL = myBasePhotoURL + restaurantJson.getPhotos().get(0).getPhoto_reference() + apiKey;
                Glide.with(this.getApplicationContext())
                        .load(myPhotoURL)
                        .into(binding.restaurantDetailPhoto);
            }

            // Set the selected restaurant fab button
            setSelectedRestaurantButtonColor();
            binding.restaurantDetailFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMemberSelectedRestaurant(restaurantJson.getPlace_id(), restaurantJson.getName());
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
            setRestaurantLikedButtonColor();
            binding.restaurantDetailLinearLayoutLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateLikedRestaurantList();
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
        });
    }

    void setRestaurantLikedButtonColor() {
        membersViewModel.getMemberLikedRestaurants().observe(this, strings -> {
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
            membersViewModel.deleteLikedRestaurant(activeMember, restaurantId);
        } else {
            membersViewModel.addLikedRestaurant(activeMember, restaurantId);
        }
        membersViewModel.initMemberLikedRestaurantsList(activeMember);
    }

    void setSelectedRestaurantButtonColor() {
        membersViewModel.getActiveMember().observe(this, member -> {
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
            membersViewModel.updateMember(activeMember);
            boolean isInSelectedList = false;
            for (int i=0; i<selectedRestaurantList.size(); i++) {
                if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                    isInSelectedList = true;
                }
            }
            if (!isInSelectedList) {
                SelectedRestaurant mySelectedRestaurant =  new SelectedRestaurant(selectedRestaurantId, selectedRestaurantName);
                membersViewModel.addSelectedRestaurant(mySelectedRestaurant);
            }
            membersViewModel.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
        } else {
            if (activeMember.getSelectedRestaurantId().equals(selectedRestaurantId)) {
                activeMember.setSelectedRestaurantId("");
                activeMember.setSelectedRestaurantName("");
                membersViewModel.updateMember(activeMember);
                membersViewModel.deleteMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
                membersViewModel.getSelectedRestaurantMembers().observe(this, members -> {
                    membersJoiningList = new ArrayList<>(members);
                    if (membersJoiningList.size() == 0) {
                        membersViewModel.deleteSelectedRestaurant(selectedRestaurantId);
                    }
                });
            } else {
                membersViewModel.deleteMemberToSelectedRestaurantMemberList(activeMember, activeMember.getSelectedRestaurantId());
                membersViewModel.getActiveMemberSelectedRestaurantMembersJoiningList(activeMember.getSelectedRestaurantId()).observe(this, members -> {
                    List<Member> myJoiningList = new ArrayList<>(members);
                    if (myJoiningList != null) {
                        if (myJoiningList.size() == 0) {
                            membersViewModel.deleteSelectedRestaurant(activeMember.getSelectedRestaurantId());
                        }
                        activeMember.setSelectedRestaurantId(selectedRestaurantId);
                        activeMember.setSelectedRestaurantName(selectedRestaurantName);
                        membersViewModel.updateMember(activeMember);
                        boolean isInSelectedList = false;
                        for (int i=0; i<selectedRestaurantList.size(); i++) {
                            if (selectedRestaurantList.get(i).getRestaurantId().equals(selectedRestaurantId)) {
                                isInSelectedList = true;
                            }
                        }
                        if (!isInSelectedList) {
                            SelectedRestaurant mySelectedRestaurant =  new SelectedRestaurant(selectedRestaurantId, selectedRestaurantName);
                            membersViewModel.addSelectedRestaurant(mySelectedRestaurant);
                        }
                        membersViewModel.addMemberToSelectedRestaurantMemberList(activeMember, selectedRestaurantId);
                    }
                });
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
        membersViewModel.getSelectedRestaurantMembers().observe(this, members -> {
            membersJoiningList = new ArrayList<>(members);
            recyclerView.setAdapter(new RestaurantDetailRecyclerViewAdapter(membersJoiningList));
        });
    }
}
