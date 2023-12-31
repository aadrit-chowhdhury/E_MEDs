package com.talhachaudhry.medicine.login_details;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talhachaudhry.medicine.admin.AdminActivity;
import com.talhachaudhry.medicine.models.UserModel;
import com.talhachaudhry.medicine.wholesaler.MainActivity;
import com.talhachaudhry.medicine.databinding.ActivityLoginBinding;

import java.util.Objects;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Logging in to your account");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("341451991468-pu129u4amf23jaf8ll2rh0ok9det9gjl.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.loginBtn.setOnClickListener(v ->
                handleLogin()
        );

        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(Login.this, MainActivity.class));
        }

        binding.forgetPassword.setOnClickListener(view -> {
            if (!binding.EmailLogin.getText().toString().trim().equals("")) {
                auth.sendPasswordResetEmail(binding.EmailLogin.getText().toString().trim()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link send to email" +
                                        binding.EmailLogin.getText().toString().trim()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Enter Email first", Toast.LENGTH_SHORT).show();
            }
        });

        binding.signUpTv.setOnClickListener(v -> startActivity(new Intent(Login.this, SignUp.class)));

        binding.googleBtnLogin.setOnClickListener(v -> signIn());
    }

    private void handleLogin() {
        if (binding.EmailLogin.getText().toString().equals("Admin") &&
                binding.PasswordLogin.getText().toString().equals("1234567")) {
            startActivity(new Intent(Login.this, AdminActivity.class));
        } else if (!binding.EmailLogin.getText().toString().trim().equals("") &&
                !binding.PasswordLogin.getText().toString().trim().equals("")) {
            progressDialog.show();
            try {
                auth.signInWithEmailAndPassword(binding.EmailLogin.getText().toString().trim(), binding.PasswordLogin.getText().toString()).
                        addOnCompleteListener(task -> {
                            progressDialog.dismiss();
                            FirebaseUser verify = auth.getCurrentUser();
                            if (task.isSuccessful()) {
                                if (verify.isEmailVerified()) {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                } else {
                                    Toast.makeText(Login.this, "Verify your Email to login", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.forgetPassword.setVisibility(View.VISIBLE);
                                Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        });
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Login.this, "Email or Password is required", Toast.LENGTH_SHORT).show();
        }

    }

    private static final int RC_SIGN_IN = 70;

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "yp " + e.getMessage(), e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        database.getReference().child("Users").
                                addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(user.getUid())) {
                                            UserModel users = new UserModel();
                                            users.setUserName(user.getDisplayName());
                                            users.setProfilePic(user.getPhotoUrl().toString());
                                            users.setContact(user.getPhoneNumber());
                                            users.setEmail(user.getEmail());
                                            users.setUserId(auth.getUid());
                                            database.getReference().child("Users").child(user.getUid()).setValue(users);
                                        }
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // do nothing
                                    }
                                });
                    } else {
                        Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        this.finishAffinity();
    }
}