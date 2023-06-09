package fr.zante.go4lunch.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import fr.zante.go4lunch.MainActivity;
import fr.zante.go4lunch.R;
import fr.zante.go4lunch.databinding.ActivityLoginBinding;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.ui.ViewModelFactory;
import fr.zante.go4lunch.ui.registration.RegisterActivity;
import fr.zante.go4lunch.ui.twitter.TwitterActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSigninClient;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSigninClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        // Register redirection
        binding.signInTextviewNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Forget password
        binding.signInTextviewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO FORGET PASSWORD BUTTON
                Toast.makeText(LoginActivity.this, "Upcoming feature !", Toast.LENGTH_SHORT).show();
            }
        });

        // CUSTOM SIGN IN
        binding.signInButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.signInEdittextName.getText().toString();
                String password = binding.signInEdittextPassword.getText().toString();
                if (email.isEmpty()) {
                    binding.signInEdittextName.setError("Invalid email format");
                } else if (password.isEmpty() || password.length()<6) {
                    binding.signInEdittextPassword.setError("Password must contain at least 6 characters");
                } else {
                    signInWithEmailAndPassword(email, password);
                }
            }
        });

        // GOOGLE SIGN IN
        binding.signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSigninClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        // TWITTER SIGN IN
        binding.signInButtonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, TwitterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // FACEBOOK SIGN IN
        binding.signInButtonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO FACEBOOK SIGN IN
                Toast.makeText(LoginActivity.this, "Will allow you to connect with Facebook", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // login success
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userEmail = firebaseUser.getEmail();
                        // Check if new user:
                        if (authResult.getAdditionalUserInfo().isNewUser()) {
                            // new user, account created:
                            addMemberToDatabase(firebaseUser);
                            Toast.makeText(LoginActivity.this, "Account created ... \n" + userEmail, Toast.LENGTH_SHORT).show();
                        } else {
                            // existing user logged in
                            Toast.makeText(LoginActivity.this, "Welcome back ... \n" + userEmail, Toast.LENGTH_SHORT).show();
                        }
                        // Start MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: login failed " + e.getMessage());
                    }
                });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Connexion réussie avec e-mail et mot de passe
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String userEmail = firebaseUser.getEmail();
                        Toast.makeText(LoginActivity.this, "Bienvenue...\n" + userEmail, Toast.LENGTH_SHORT).show();
                        // Démarrer MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: Échec de la connexion " + e.getMessage());
                    }
                });
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

        LoginViewModel loginViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(LoginViewModel.class);
        loginViewModel.addMember(member);
    }
}