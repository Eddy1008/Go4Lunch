package fr.zante.go4lunch;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import fr.zante.go4lunch.databinding.ActivityMainBinding;
import fr.zante.go4lunch.databinding.NavHeaderMainBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.RestaurantJson;
import fr.zante.go4lunch.notification.AlarmReceiver;
import fr.zante.go4lunch.notification.NotificationService;
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
    private List<Member> activeMemberSelectedRestaurantMemberList = new ArrayList<>();
    private String myInfo;

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
                /** TODO
                createNotificationChannel();
                getDailyNotification();
                 */
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





    /** TODO
    private void createNotificationChannel() {
        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Firebase Messages";
            String channelDescription = "Channel for alarm manager Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel("myNotificationChannel", channelName, importance);
            mChannel.setDescription(channelDescription);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    // notification journalieres:
    private void getDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        //getActiveMemberSelectedRestaurantMembersInfo(activeMember.getSelectedRestaurantId());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("NOTIFICATION_MEMBER_NAME", activeMember.getName());
        myBundle.putString("NOTIFICATION_SELECTED_RESTAURANT_ID", activeMember.getSelectedRestaurantId());
        //myBundle.putString("NOTIFICATION_INFOS", myInfo);
        intent.putExtra("BUNDLE_NOTIFICATION_INFO", myBundle);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // TODO PB POUR REPETITION DE LA NOTIFICATION CHAQUE JOUR
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Log.d("TAG", "getDailyNotification: La notification sera envoyée le : \n "
                + calendar.get(Calendar.DAY_OF_MONTH) + "/" +  calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR)
                + " \n à : " + calendar.get(Calendar.HOUR_OF_DAY) + "h" +  calendar.get(Calendar.MINUTE));
    }

    private void getActiveMemberSelectedRestaurantMembersInfo(String activeMemberSelectedRestaurantId) {
        membersViewModel.getSelectedRestaurantMembersJoiningList(activeMemberSelectedRestaurantId).observe(this, members -> {
            activeMemberSelectedRestaurantMemberList = new ArrayList<>(members);
            if (activeMemberSelectedRestaurantMemberList.size() == 0) {
                myInfo = "Vous n'avez pas fait votre choix pour ce midi !";
            } else if (activeMemberSelectedRestaurantMemberList.size() == 1) {
                myInfo = "Vous mangerez seul ce midi !";
            } else {
                String memberListString = "";
                for (int i=0; i<activeMemberSelectedRestaurantMemberList.size(); i++) {
                    Log.d("TAG", "getActiveMemberSelectedRestaurantMembersInfo: joining member : " + activeMemberSelectedRestaurantMemberList.get(i).getName());
                    if (memberListString.equals("")) {
                        if (!Objects.equals(activeMemberSelectedRestaurantMemberList.get(i).getMemberId(), activeMember.getMemberId())) {
                            memberListString = activeMemberSelectedRestaurantMemberList.get(i).getName();
                        }
                    } else {
                        if (!Objects.equals(activeMemberSelectedRestaurantMemberList.get(i).getMemberId(), activeMember.getMemberId())) {
                            memberListString = memberListString + ", " + activeMemberSelectedRestaurantMemberList.get(i).getName();
                        }
                    }
                }
                myInfo = activeMemberSelectedRestaurantMemberList.size() + " collègue(s) se joignent à vous ce midi: " + memberListString;
            }
            Log.d("TAG", "getActiveMemberSelectedRestaurantMembersInfo: my info = " + myInfo);
        });
    }
     */
}