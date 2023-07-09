package fr.zante.go4lunch.notification;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
        try {
            getActiveMemberFromFirebase();
            return Result.success();
        } catch (Exception e) {
            Log.d("TAG", "doWork: exception = " + e.getMessage());
            return Result.failure();
        }
    }

    private void showNotification(String toDisplayInNotification) {
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
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data: snapshot.getChildren()) {
                        Member member = data.getValue(Member.class);
                        if (member != null) {
                            if (member.getName().equals(firebaseUser.getDisplayName())) {
                                activeMember = member;
                            }
                        }
                    }
                    getDataFromFirebase(activeMember.getSelectedRestaurantId(), activeMember.getMemberId());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("getActiveMember()", "onCancelled: read database failed");
                }
            });
        } else {
            Log.d("TAG", "NOTIFICATION_WORK_MANAGER : getActiveMemberFromFirebase: firebaseUser = NULL ");
        }
    }

    private void getDataFromFirebase(String selectedRestaurantId, String activeMemberId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mySelectedRestaurantMemberListRef = database.getReference("selectedRestaurants").child(selectedRestaurantId).child("restaurantSelectedBy");

        List<Member> myMembersList = new ArrayList<>();
        firebaseData = "";

        mySelectedRestaurantMemberListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myMembersList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        myMembersList.add(member);
                    }
                }
                if (myMembersList.size() == 0) {
                    firebaseData = "Vous n'avez pas fait votre choix pour ce midi !";
                } else if (myMembersList.size() == 1) {
                    firebaseData = "Vous mangerez seul ce midi !";
                } else {
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
                    firebaseData =" Ce midi, vous mangerez à " + myMembersList.size() + "! Toi et " + memberListString;
                }
                showNotification(firebaseData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: " + error.getMessage());
            }
        });
    }
}
