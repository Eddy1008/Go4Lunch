package fr.zante.go4lunch.notification;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.R;
import fr.zante.go4lunch.model.Member;

public class NotificationWorkManager extends Worker {

    private String firebaseData;
    private Member activeMember;

    public NotificationWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("TAG", "doWork:");
        try {
            Log.d("TAG", "NOTIFICATION_WORK_MANAGER : doWork: appel de la méthode getActiveMemberFromFirebase() ");
            getActiveMemberFromFirebase();

            return Result.success();
        } catch (Exception e) {
            Log.d("TAG", "doWork: exception = " + e.getMessage());
            return Result.failure();
        }
    }

    private void showNotification(String toDisplayInNotification) {
        Log.d("TAG", "showNotification: AFFICHE LA NOTIF AVEC LES INFOS RECUES : " + toDisplayInNotification);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "myNotificationChannel")
                .setContentTitle("Bon appétit")
                .setContentText(toDisplayInNotification)
                .setSmallIcon(R.drawable.ic_baseline_android_24)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(1, builder.build());
    }

    private void getActiveMemberFromFirebase() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("members");

        if (firebaseUser != null) {
            Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase: recupération du firebaseUser != NULL : \n user = " + firebaseUser.getDisplayName());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data: snapshot.getChildren()) {
                        Member member = data.getValue(Member.class);
                        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase : onDataChange: member.getName() = " + member.getName());
                        if (member != null) {
                            if (member.getName().equals(firebaseUser.getDisplayName())) {
                                activeMember = member;
                            }
                        }
                    }
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase : onDataChange : Enregistrement de l'activeMember = " + activeMember.getName());
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase : onDataChange : appel de la méthode getDataFromFirebase() avec les parametres : " +
                            "\n activeMember.getSelectedRestaurantId() = " + activeMember.getSelectedRestaurantId() +
                            "\n activeMember.getMemberId() = " + activeMember.getMemberId() );
                    getDataFromFirebase(activeMember.getSelectedRestaurantId(), activeMember.getMemberId());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("getActiveMember()", "onCancelled: read database failed");
                }
            });
        } else {
            Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase: recupération du firebaseUser : \n user = NULL ");
        }
    }

    private void getDataFromFirebase(String selectedRestaurantId, String activeMemberId) {
        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase: parametre reçus : " +
                "\n String selectedRestaurantId = " + selectedRestaurantId +
                "\n String activeMemberId = " + activeMemberId );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mySelectedRestaurantMemberListRef = database.getReference("selectedRestaurants").child(selectedRestaurantId).child("restaurantSelectedBy");

        List<Member> myMembersList = new ArrayList<>();
        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase: création liste vide : myMembersList.size() = " + myMembersList.size());

        firebaseData = "message vide normalement ...";
        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase: création string affiché dans notif : firebaseData = message vide normalement ... = " + firebaseData);

        mySelectedRestaurantMemberListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myMembersList.clear();
                Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : myMembersList.clear() = " + myMembersList.size());
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : membre ayant selectionné cerestaurant : member.getName() = " + member.getName());
                        myMembersList.add(member);
                        Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : ajout de ce membre à la liste : myMembersList.size() = " + myMembersList.size());
                    }
                }
                if (myMembersList.size() == 0) {
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : myMembersList.size() = 0 ? " + myMembersList.size());
                    firebaseData = "Vous n'avez pas fait votre choix pour ce midi !";
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange :  message notif = " + firebaseData);
                } else if (myMembersList.size() == 1) {
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : myMembersList.size() = 1 ? " + myMembersList.size());
                    firebaseData = "Vous mangerez seul ce midi !";
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange :  message notif = " + firebaseData);
                } else {
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : myMembersList.size() = 2+ ? " + myMembersList.size());
                    String memberListString = "";
                    for (int i=0; i<myMembersList.size(); i++) {
                        if (memberListString.equals("")) {
                            if (!myMembersList.get(i).getMemberId().equals(activeMemberId)) {
                                memberListString = myMembersList.get(i).getName();
                            }
                        } else {
                            if (!myMembersList.get(i).getMemberId().equals(activeMemberId)) {
                                memberListString = memberListString + ", " + myMembersList.get(i).getName();
                            }
                        }
                    }
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : liste des participants contenus dans message = " + memberListString);
                    firebaseData =" Ce midi, vous mangerez à " + myMembersList.size() + "! Toi et " + memberListString;
                    Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange :  message notif = " + firebaseData);
                }
                Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getDataFromFirebase : onDataChange : appel de la méthode showNotification() ");
                showNotification(firebaseData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }

}
