package fr.zante.go4lunch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.List;

import fr.zante.go4lunch.data.MemberRepository;
import fr.zante.go4lunch.databinding.ActivityMainBinding;
import fr.zante.go4lunch.databinding.NavHeaderMainBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.login.LoginActivity;
import fr.zante.go4lunch.ui.restaurantdetail.RestaurantActivity;
import fr.zante.go4lunch.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;
    private SharedViewModel sharedViewModel;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        getUserInfo();

        MemberRepository memberRepository = MemberRepository.getInstance();
        memberRepository.updateData();

        this.sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setMyUserId(userId);

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

        binding.navView.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Member member = memberRepository.getMemberById(userId);
                Log.d("TAG", "onCreate: userId = " + userId + " member.getMemberId() = " + member.getMemberId());

                Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                Bundle myBundle = new Bundle();
                // TODO utiliser les données du member actif : member.getSelectedRestaurantId()
                myBundle.putString("RESTAURANT_PLACE_ID", member.getSelectedRestaurantId());
                myBundle.putString("USER_ID", userId);
                intent.putExtra("BUNDLE_RESTAURANT_SELECTED", myBundle);
                startActivity(intent);
                return false;
            }
        });

        binding.navView.getMenu().getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return false;
            }
        });
        
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
            // error : user not logged
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            String userEmail = firebaseUser.getEmail();
            String userName = firebaseUser.getDisplayName();
            NavHeaderMainBinding navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0));
            navHeaderMainBinding.textViewMail.setText(userEmail);
            navHeaderMainBinding.textViewName.setText(userName);
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .apply(new RequestOptions().override(200, 200))
                    .into(navHeaderMainBinding.imageViewAvatar);

            userId = firebaseUser.getUid();
            Log.d("TAG", "getUserInfo: Uid = " + userId);
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