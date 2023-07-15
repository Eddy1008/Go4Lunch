package fr.zante.go4lunch.ui.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import fr.zante.go4lunch.MainActivity;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.databinding.ActivityRegisterBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.ViewModelFactory;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        // Login redirection
        binding.signInTextviewExistingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Register Button
        binding.signInButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewMember();
            }
        });
    }

    private void registerNewMember() {
        String email = binding.signInEdittextMail.getText().toString();
        String name = binding.signInEdittextName.getText().toString();
        String password = binding.signInEdittextPassword.getText().toString();
        String confirmPassword = binding.signInEdittextConfirmPassword.getText().toString();

        String emailPattern = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";

        if (email.matches(emailPattern) || email.isEmpty()) {
            binding.signInEdittextMail.setError(getString(R.string.register_activity_mail_error));
        } else if (name.isEmpty() || name.length()<4) {
            binding.signInEdittextName.setError(getString(R.string.register_activity_name_error));
        } else if (password.isEmpty() || password.length()<6) {
            binding.signInEdittextPassword.setError(getString(R.string.register_activity_password_error));
        } else if (!password.equals(confirmPassword)) {
            binding.signInEdittextConfirmPassword.setError(getString(R.string.register_activity_confirm_password_error));
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(Uri.parse("https://cdn.pixabay.com/photo/2016/04/30/05/04/camera-1362419_960_720.jpg"))
                                .build();

                        if (user != null) {
                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        addMemberToDatabase(user);
                                    }
                                }
                            });
                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                        @Override
                                        public void onSuccess(AuthResult authResult) {
                                            Toast.makeText(RegisterActivity.this, getString(R.string.register_activity_new_account)+ "\n" + user.getEmail(), Toast.LENGTH_SHORT).show();
                                            // Start MainActivity
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("TAG", "onFailure: connexion failed " + e.getMessage());
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
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
                "",
                false
        );

        RegisterViewModel registerViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RegisterViewModel.class);
        registerViewModel.addMember(member);
    }
}