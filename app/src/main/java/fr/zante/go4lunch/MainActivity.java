package fr.zante.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import fr.zante.go4lunch.databinding.ActivityMainBinding;
import fr.zante.go4lunch.databinding.NavHeaderMainBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.MembersViewModel;
import fr.zante.go4lunch.ui.ViewModelFactory;
import fr.zante.go4lunch.ui.login.LoginActivity;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;
import fr.zante.go4lunch.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private MembersViewModel membersViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private String userName;
    private Member activeMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        // Data:
        membersViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MembersViewModel.class);

        // Authentication:
        firebaseAuth = FirebaseAuth.getInstance();
        getUserInfo();

        if (userName != null) {
            membersViewModel.initActiveMember(userName);
            membersViewModel.getActiveMember().observe(this, member -> {
                activeMember = member;
            });
        }

        // UI Settings:
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_mapview, R.id.nav_listview, R.id.nav_workmates)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Set YOUR LUNCH menu item
        binding.navView.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                if (activeMember.getSelectedRestaurantId().equals("")) {
                    Toast.makeText(MainActivity.this, getString(R.string.make_a_choice), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                    Bundle myBundle = new Bundle();
                    myBundle.putString("RESTAURANT_PLACE_ID", activeMember.getSelectedRestaurantId());
                    myBundle.putString("USER_NAME", activeMember.getName());
                    intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                    startActivity(intent);
                }
                return false;
            }
        });

        // Set Settings menu item
        binding.navView.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return false;
            }
        });

        // Set LOGOUT menu item
        binding.navView.getMenu().getItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                firebaseAuth.signOut();
                GoogleSignInOptions gso = new GoogleSignInOptions
                        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                GoogleSignIn.getClient(MainActivity.this, gso).signOut();
                getUserInfo();
                return false;
            }
        });
    }

    private void getUserInfo() {
        // Get user:
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // user not logged
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            NavHeaderMainBinding navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
            navHeaderMainBinding.textViewMail.setText(firebaseUser.getEmail());
            navHeaderMainBinding.textViewName.setText(firebaseUser.getDisplayName());
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .apply(new RequestOptions().override(200, 200))
                    .into(navHeaderMainBinding.imageViewAvatar);
            // set userName value for sending in RestaurantActivity
            userName = firebaseUser.getDisplayName();
            // Register the user Name in ViewModel (need in ListviewFragment)
            membersViewModel.setMyUserName(userName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchViewBox = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewBox.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                membersViewModel.getFilteredRestaurants(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                membersViewModel.getFilteredRestaurants(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}