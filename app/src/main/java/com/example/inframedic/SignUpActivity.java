package com.example.inframedic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.inframedic.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    //Firebase essentials
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    //UI
    private EditText usernameEt;
    private AutoCompleteTextView emailEt;
    private EditText passwordEt;
    private Button signUpButton;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Does nothing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).hide();

        //Firebase essentials setup
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };
        currentUser = firebaseAuth.getCurrentUser();

        //Registering UI elements
        usernameEt = findViewById(R.id.signup_username_et);
        emailEt = findViewById(R.id.signup_email_et);
        passwordEt = findViewById(R.id.signup_password_et);
        signUpButton = findViewById(R.id.signup_signup_button);
        loginButton = findViewById(R.id.signup_login_button);
        progressBar = findViewById(R.id.progressBar_signup);

        progressBar.setVisibility(View.INVISIBLE);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(usernameEt.getText().toString().trim()) &&
                    !TextUtils.isEmpty(passwordEt.getText().toString().trim()) &&
                        !TextUtils.isEmpty(passwordEt.getText().toString().trim())) {

                    String username = usernameEt.getText().toString().trim();
                    String email = emailEt.getText().toString().trim();
                    String password = passwordEt.getText().toString().trim();
                    signupWithEmailAndPassword(username, email, password);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void signupWithEmailAndPassword(String username, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);

                            collectionReference.add(userObj)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            documentReference.get()
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.getResult().exists()) {
                                                                String name = task.getResult().getString("username");

                                                                UserApi userApi = UserApi.getInstance();
                                                                userApi.setUserId(currentUserId);
                                                                userApi.setUsername(name);

                                                                Log.d("signup", "onComplete: signed in");
                                                                Intent intent = new Intent(SignUpActivity.this, Dashboard.class);
                                                                intent.putExtra("fromsignup", 1);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Log.d("signup", "onFailure: couldn't get document snapshot");
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Log.d("signup", "onFailure: couldn't add user to database");
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("signup", "onFailure: couldn't sign up new user");

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}