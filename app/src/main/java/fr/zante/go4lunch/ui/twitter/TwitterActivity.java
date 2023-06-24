package fr.zante.go4lunch.ui.twitter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import fr.zante.go4lunch.MainActivity;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class TwitterActivity extends MainActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");

        provider.addCustomParameter("lang", "fr");

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(TwitterActivity.this, MainActivity.class));
                            Toast.makeText(TwitterActivity.this, "TWITTER AAA", Toast.LENGTH_SHORT).show();
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                addMemberToDatabase(user);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TwitterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            firebaseAuth.startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(new Intent(TwitterActivity.this, MainActivity.class));
                            Toast.makeText(TwitterActivity.this, "TWITTER BBB", Toast.LENGTH_SHORT).show();
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                addMemberToDatabase(user);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure.
                            Toast.makeText(TwitterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addMemberToDatabase(FirebaseUser firebaseUser) {
        Member member = new Member(
                firebaseUser.getUid(),
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                String.valueOf(firebaseUser.getPhotoUrl()),
                "",
                ""
        );

        TwitterViewModel twitterViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(TwitterViewModel.class);
        twitterViewModel.addMember(member);
    }
}