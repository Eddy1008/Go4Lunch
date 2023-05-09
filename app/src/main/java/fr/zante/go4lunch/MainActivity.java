package fr.zante.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import fr.zante.go4lunch.databinding.ActivityMainBinding;
import fr.zante.go4lunch.databinding.NavHeaderMainBinding;
import fr.zante.go4lunch.ui.login.LoginActivity;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        getUserInfo();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_settings, R.id.nav_logout, R.id.nav_mapview, R.id.nav_listview, R.id.nav_workmates)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        binding.navView.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                Bundle myBundle = new Bundle();
                myBundle.putString("RESTAURANT_PLACE_ID", "ChIJcxi2sojVwkcRGbFnQjPp7xU");
                intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                startActivity(intent);
                return false;
            }
        });

    }

    private void getUserInfo() {
        // Get user:
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // error : user not logged
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String userEmail = firebaseUser.getEmail();
            // TODO binding navView navHeaderMain ???
            NavHeaderMainBinding navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
            navHeaderMainBinding.textViewMail.setText(userEmail);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}