package com.example.inframedic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.example.inframedic.util.UserApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Firebase essentials
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    //UI
    private AutoCompleteTextView emailET;
    private EditText passwordET;
    private Button loginButton;
    private Button signUpButton;
    private ProgressBar progressBar;

    String email, password;

//    @Override
//    public void onBackPressed() {
//        //Create the object of Alert Dialog Builder class
//        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//        Log.d("RBClick", "builder object created");
//
//        //Setting the message to be shown
//        builder.setMessage("You sure you want to exit app?");
//
//        //Set Alert title
//        builder.setTitle("Alert!");
//
//        //Set cancellable false so that when user clicks
//        //outside the box the box will still be visible
//        builder.setCancelable(false);
//
//        //Setting the positive button
//        builder.setPositiveButton("YES", (dialog, which) -> {
//            //When the user clicks "YES" the dialog box will close
//            finish();
//            dialog.dismiss();
//        });
//
//        //Setting the negative button
//        builder.setNegativeButton("NO", (dialog, which) -> {
//            //When the user clicks "NO" the dialog box will cancel
//            dialog.cancel();
//        });
//
//        //Create the alert dialog box
//        AlertDialog alertDialog = builder.create();
//
//        //Show the alert dialog box
//        alertDialog.show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Firebase essentials setup
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    final String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        return;
                                    }
                                    assert value != null;
                                    if (!value.isEmpty()) {
                                        for (QueryDocumentSnapshot snapshot : value) {
                                            UserApi userApi = UserApi.getInstance();
                                            userApi.setUserId(snapshot.getString("userId"));
                                            userApi.setUsername(snapshot.getString("username"));

                                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            finish();
                                        }
                                    }
                                }
                            });
                }
            }
        };
        currentUser = firebaseAuth.getCurrentUser();

        //Registering UI elements
        passwordET = findViewById(R.id.login_password_et);
        emailET = findViewById(R.id.login_email_et);
        signUpButton = findViewById(R.id.login_sign_up_button);
        loginButton = findViewById(R.id.login_login_button);
        progressBar = findViewById(R.id.progressBar_login);

        progressBar.setVisibility(View.INVISIBLE);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                loginWithEmailAndPassword(email, password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }

    private void loginWithEmailAndPassword(String email, String password) {
        Log.d("mainAct", "loginWithEmailAndPassword: login button pressed");
        if (!TextUtils.isEmpty(email) &&
            !TextUtils.isEmpty(password)){
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("mainAct", "onComplete: login complete");
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;
                            final String currentUserId = currentUser.getUid();

                            collectionReference.whereEqualTo("userId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            for (QueryDocumentSnapshot snapshot : value) {
                                                UserApi userApi = UserApi.getInstance();
                                                userApi.setUsername(snapshot.getString("username"));
                                                userApi.setUserId(currentUserId);

                                                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d("mainAct", "onFailure: couldn't sign in user");
                            Toast.makeText(MainActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}