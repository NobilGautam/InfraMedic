package com.example.inframedic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.inframedic.ui.RecyclerViewAdapter;
import com.example.inframedic.util.UserApi;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Issue");

    private RecyclerView recyclerView;
    private TextView noIssueTextView;
    private List<Issue> issueList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView userGreeting;

    @Override
    public void onBackPressed() {
        //Create the object of Alert Dialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
        Log.d("RBClick", "builder object created");

        //Setting the message to be shown
        builder.setMessage("Exit app without sign out?");

        //Set Alert title
        builder.setTitle("Alert!");

        //Set cancellable false so that when user clicks
        //outside the box the box will still be visible
        builder.setCancelable(false);

        //Setting the positive button
        builder.setPositiveButton("YES", (dialog, which) -> {
//            When the user clicks "YES" the dialog box will close
//            firebaseAuth.signOut();
//            UserApi userApi = UserApi.getInstance();
//            userApi.setUsername(null);
//            Intent intent = new Intent(Dashboard.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
            finish();
        });

        //Setting the negative button
        builder.setNegativeButton("NO", (dialog, which) -> {
            //When the user clicks "NO" the dialog box will cancel
            dialog.cancel();
        });

        //Create the alert dialog box
        AlertDialog alertDialog = builder.create();

        //Show the alert dialog box
        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    finish();
                }
            }
        };



        noIssueTextView = findViewById(R.id.textView);
        userGreeting = findViewById(R.id.user_greeting_tv);
        issueList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        userGreeting.setText("Hello, " + UserApi.getInstance().getUsername());




    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.add_issue_button) {
            if (currentUser != null && firebaseAuth != null) {
                Intent intent = new Intent(Dashboard.this, AddIssueActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else if (itemId == R.id.sign_out_button) {
            if (currentUser != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                UserApi userApi = UserApi.getInstance();
                userApi.setUsername(null);
                startActivity(new Intent(Dashboard.this, MainActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (UserApi.getInstance().getUsername() == null) {
            finish();
        }
        collectionReference.whereEqualTo("userId", UserApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            noIssueTextView.setVisibility(View.VISIBLE);
                        } else {
                            noIssueTextView.setVisibility(View.INVISIBLE);
                            for (QueryDocumentSnapshot issues: queryDocumentSnapshots) {
                                Issue issue = issues.toObject(Issue.class);
                                issueList.add(issue);
                            }

                            recyclerViewAdapter = new RecyclerViewAdapter(issueList, Dashboard.this);
                            recyclerView.setAdapter(recyclerViewAdapter);
                            recyclerViewAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }
}