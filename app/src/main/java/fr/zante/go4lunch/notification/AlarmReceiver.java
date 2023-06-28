package fr.zante.go4lunch.notification;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.zante.go4lunch.MainActivity;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;

public class AlarmReceiver extends BroadcastReceiver {

    private String activeMemberName;
    private String selectedRestaurantId;
    private List<Member> joiningMemberList;
    private String notificationInfoToDisplay;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentToMain = new Intent(context, MainActivity.class);
        Bundle myBundle = intent.getBundleExtra("BUNDLE_NOTIFICATION_INFO");
        activeMemberName = (String) myBundle.get("NOTIFICATION_MEMBER_NAME");
        selectedRestaurantId = (String) myBundle.get("NOTIFICATION_SELECTED_RESTAURANT_ID");
        notificationInfoToDisplay = "Ca ne marche pas ...";

        /**
        MembersRepository repository = MembersRepository.getInstance();
        repository.getSelectedRestaurantMemberLiveDataList(selectedRestaurantId).observe(probleme, members -> {
            joiningMemberList = new ArrayList<>(members);
            Log.d("TAG", "onReceive: joiningMembersList.size() = " + joiningMemberList.size());
            if (joiningMemberList.size() == 0) {
                notificationInfoToDisplay = "Vous n'avez pas fait votre choix pour ce midi !";
            } else if (joiningMemberList.size() == 1) {
                notificationInfoToDisplay = "Vous mangerez seul ce midi !";
            } else {
                String memberListString = "";
                for (int i=0; i<joiningMemberList.size(); i++) {
                    Log.d("TAG", "getActiveMemberSelectedRestaurantMembersInfo: joining member : " + joiningMemberList.get(i).getName());
                    if (memberListString.equals("")) {
                        if (!Objects.equals(joiningMemberList.get(i).getName(), activeMemberName)) {
                            memberListString = joiningMemberList.get(i).getName();
                        }
                    } else {
                        if (!Objects.equals(joiningMemberList.get(i).getName(), activeMemberName)) {
                            memberListString = memberListString + ", " + joiningMemberList.get(i).getName();
                        }
                    }
                }
                notificationInfoToDisplay = joiningMemberList.size() + " collègue(s) se joignent à vous ce midi: " + memberListString;
            }
            Log.d("TAG", " my notification info = " + notificationInfoToDisplay);
        });
         */

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentToMain, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "myNotificationChannel")
                .setSmallIcon(R.drawable.ic_baseline_android_24)
                .setContentTitle(activeMemberName)
                .setContentText(selectedRestaurantId) // notificationInfoToDisplay
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
        Log.d("TAG", "onReceive: La notification est envoyée !!! ");
    }
}
