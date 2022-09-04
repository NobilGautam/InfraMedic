package com.example.inframedic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inframedic.util.Hospital;
import com.example.inframedic.util.UserApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AddIssueActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int GALLERY_CODE = 1;
    private static final int CAMERA_CODE = 2;
    private static final int PERM_REQUEST_CODE = 3;
    private ImageView imageView;
    private ImageButton cameraButton;
    private TextView hospitalAddress;
    private EditText description;
    private Button reportButton;
    private ProgressBar progressBar;
    private Spinner spinner;
    private EditText roomNo;
    private EditText department;
    private EditText wardNo;
    private CardView popUp;
    private Button popUpOkButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Issue");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private StorageReference storageReference;

    private Uri imageUri;
    private String currentPhotoPath;

    private Boolean cameraPerm, readStoragePerm, writeStoragePerm;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddIssueActivity.this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_issue);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        //Registering UI elements
        imageView = findViewById(R.id.imageView);
        cameraButton = findViewById(R.id.choose_img_button);
        hospitalAddress = findViewById(R.id.address_et);
        description = findViewById(R.id.description_et);
        roomNo = findViewById(R.id.room_no_et);
        wardNo = findViewById(R.id.ward_no_et);
        department = findViewById(R.id.department_name_et);
        reportButton = findViewById(R.id.report_button);
        progressBar = findViewById(R.id.progressBar_add_issue);
        spinner = findViewById(R.id.spinner);
        popUp = findViewById(R.id.pop_up_add_activity);
        popUpOkButton = findViewById(R.id.ok_button);

        popUp.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        progressBar.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.hospital_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //alert dialog box


                AlertDialog.Builder builder = new AlertDialog.Builder(AddIssueActivity.this);
                Log.d("RBClick", "builder object created");

                //Setting the message to be shown
                builder.setMessage("Add image describing issue");

                //Set Alert title
                builder.setTitle("Dear User,");

                //Set cancellable false so that when user clicks
                //outside the box the box will still be visible
                builder.setCancelable(false);

                //Setting the positive button
                builder.setPositiveButton("Choose from device", (dialog, which) -> {
                    //When the user clicks "YES" the dialog box will close
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_CODE);
                    dialog.dismiss();
                });

                builder.setNegativeButton("Capture Now", (dialogInterface, i) -> {
                    askCameraStoragePermissions();
                    dialogInterface.dismiss();
                });

                //Create the alert dialog box
                AlertDialog alertDialog = builder.create();

                //Show the alert dialog box
                alertDialog.show();
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!spinner.getSelectedItem().toString().equals("") &&
                        !TextUtils.isEmpty(hospitalAddress.getText().toString().trim()) &&
                        !TextUtils.isEmpty(description.getText().toString().trim()) &&
                        imageUri != null) {

                    progressBar.setVisibility(View.VISIBLE);

                    StorageReference filepath = storageReference
                            .child("issue_images")
                            .child("image_" + Timestamp.now().getSeconds());

                    filepath.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    filepath.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Issue issue = new Issue();
                                                    String hospital_name = spinner.getSelectedItem().toString();
                                                    issue.setHospitalName(hospital_name);
                                                    issue.setHospitalAddress(hospitalAddress.getText().toString().trim());
                                                    issue.setIssueDescription(description.getText().toString().trim());
                                                    issue.setIssueRoomNo(roomNo.getText().toString().trim());
                                                    issue.setIssueWardNo(wardNo.getText().toString().trim());
                                                    issue.setIssueDepartment(department.getText().toString().trim());
                                                    issue.setImageUrl(uri.toString());
                                                    UserApi userApi = UserApi.getInstance();
                                                    String username = userApi.getUsername();
                                                    String userId = userApi.getUserId();
                                                    issue.setUsername(username);
                                                    issue.setUserId(userId);

                                                    collectionReference.add(issue)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    popUp.setVisibility(View.VISIBLE);
                                                                    popUpOkButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            Intent intent = new
                                                                                    Intent(AddIssueActivity.this,
                                                                                    Dashboard.class);
                                                                            popUp.setVisibility(View.INVISIBLE);
                                                                            startActivity(intent);
                                                                            finish();
                                                                        }
                                                                    });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            }
        });
    }

    private void askCameraStoragePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            cameraPerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            readStoragePerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            writeStoragePerm = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

            ArrayList<String> permissions = new ArrayList<>();
            if (!cameraPerm) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (!readStoragePerm) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(!writeStoragePerm) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            String[] permissionsToRequest = new String[permissions.size()];
            permissions.toArray(permissionsToRequest);

            ActivityCompat.requestPermissions(this, permissionsToRequest, PERM_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (readStoragePerm) {
                if (writeStoragePerm) {
                    dispatchTakePictureIntent();
                }
            }
        } else {
            Toast.makeText(AddIssueActivity.this, "Permissions required!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            }
        }

        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {
            imageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
        currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Log.d("spinner", "onItemSelected: item selected");
        String hospitalName = adapterView.getItemAtPosition(position).toString();
        hospitalAddress.setText(Hospital.getHospitals().get(hospitalName));
        Log.d("spinner", "onItemSelected: " + Hospital.getHospitals().get(hospitalName));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private File createImageFile() throws IOException {
        //Creating an image file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timestamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        //Save a file path for use for ACTION_VIEW intents
        //Generally a reference if you wish to make image available to gallery
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensuring that there is a camera activity to handle the intent
        if (captureImageIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {
                //Error occurred while creating the file
            }

            //Ensuring that file was successfully created
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this,
                        "com.example.inframedic.fileprovider", photoFile);
                captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(captureImageIntent, CAMERA_CODE);
            }
        }
    }
}