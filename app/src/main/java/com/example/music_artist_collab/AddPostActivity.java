package com.example.music_artist_collab;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import android.Manifest;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseDatabase userDbRef;
    EditText titleEt,descriptionEt;
    ImageView imageView;
    Button uplaodBtn;
    Uri image_uri=null, videoUri = null;

    String name,email,uid,dp;
    ProgressDialog pd;

    static final int CAMERA_REQUEST_CODE=100;
    static final int STORAGE_REQUEST_CODE=200;
    static final int IMAGE_PICK_CAMERA_CODE=300;
    static final int IMAGE_PICK_GALLERY_CODE=400;
    static final int VIDEO_PICK_GALLERY_CODE = 500;


    String []cameraPermission;
    String [] storagePermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        cameraPermission=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth=FirebaseAuth.getInstance();

        checkUserStatus();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        // Add a ValueEventListener to fetch user data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   name= dataSnapshot.child("name").getValue(String.class);
                   dp = dataSnapshot.child("image").getValue(String.class);
//                    name.setText(userName);
//                    dp.setText(userEmail);

                    if (dataSnapshot.hasChild("image")) {
                        String profileImageUrl = dataSnapshot.child("image").getValue(String.class);
//                        Picasso.get().load(profileImageUrl).into(profileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur while fetching user data
            }
        });

        userDbRef= FirebaseDatabase.getInstance().getReference("users").getDatabase();
        Query query=userDbRef.getReference().orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()) {
                    Log.d("PostData", "name: " + ds.child("name").getValue());
                    Log.d("PostData", "email: " + ds.child("email").getValue());
                    Log.d("PostData", "Image: " + ds.child("Image").getValue());

                    name=""+ds.child("name").getValue();
                    email=""+ds.child("email").getValue();
                    dp=""+ds.child("Image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        titleEt = findViewById(R.id.pTitleEt);
        descriptionEt = findViewById(R.id.pDescriptionEt); // Update variable name to match the XML layout
        imageView = findViewById(R.id.pImageIv);
        uplaodBtn = findViewById(R.id.pUploadBtn);
        pd = new ProgressDialog(this);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowImagePickDialog();
            }


        });


        uplaodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title=titleEt.getText().toString().trim();
                String description=  descriptionEt.getText().toString().trim();
                if(TextUtils.isEmpty(title)){
                    Toast.makeText(AddPostActivity.this, "Enter title", Toast.LENGTH_SHORT).show();
                    return;
                } if(TextUtils.isEmpty(description)){
                    Toast.makeText(AddPostActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (videoUri != null) {
                    // Handle video upload here
                    uplaodVideoData(title, description, videoUri);
                } else if(image_uri==null){
                    uplaodData(title,description,"noImage");

                }else{
                    uplaodData(title,description,String.valueOf(image_uri));

                }
            }
        });
    }
    private void uplaodVideoData(String title, String description, final Uri videoUri) {
        pd.setMessage("Uploading video...");
        pd.show();
        final String timeStamp = String.valueOf(System.currentTimeMillis());

        // Create a unique file path and name for the video
        final String filePathAndName = "Videos/" + "video_" + timeStamp;

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUri = uri.toString();

                                // Create a HashMap to store video details
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("uName", name);
                                hashMap.put("uEmail", email);
                                hashMap.put("uDp", dp);
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pDescr", description);
                                hashMap.put("pVideo", downloadUri); // Store the video download URL
                                hashMap.put("uTime", timeStamp);

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();

                                                // Clear the UI and reset variables
                                                titleEt.setText("");
                                                descriptionEt.setText("");
                                                // Reset the videoUri here if needed
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, "Video upload failed: " + e.getMessage(),  Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uplaodData(String title, String description, String uri) {
        pd.setMessage("Publishing post....");
        pd.show();
        String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Posts/" + "post_" + timeStamp;


        if (!uri.equals("noImage")) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {


                                    String downloadUri = uri.toString();
                                    HashMap<Object, String> hashmap = new HashMap<>();
                                    hashmap.put("uid", uid);
                                    hashmap.put("uName", name);
                                    hashmap.put("uEmail", email);
                                    hashmap.put("uDp", dp);
                                    hashmap.put("pId", timeStamp);
                                    hashmap.put("pTitle", title);
                                    hashmap.put("pDescr", description);
                                    hashmap.put("pImage", downloadUri);
                                    hashmap.put("uTime", timeStamp);

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                    ref.child(timeStamp).setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();
                                            titleEt.setText("");
                                            descriptionEt.setText("");
                                            imageView.setImageURI(null);
                                            image_uri = null;
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            HashMap<Object, String> hashmap = new HashMap<>();
            hashmap.put("uid", uid);
            hashmap.put("uName", name);
            hashmap.put("uEmail", email);
            hashmap.put("uDp", dp);
            hashmap.put("pId", timeStamp);
            hashmap.put("pTitle", title);
            hashmap.put("pDescr", description);
            hashmap.put("pImage", "noImage");
            hashmap.put("uTime", timeStamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timeStamp).setValue(hashmap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, "Post Published", Toast.LENGTH_SHORT).show();
                    titleEt.setText("");
                    descriptionEt.setText("");
                    imageView.setImageURI(null);
                    image_uri = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void ShowImagePickDialog() {
        String []options={"Camera","Gallery Photo","Gallery video"};
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        PickFromCamera();
                    }
                }if(i==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        PickFromGallery();
                    }
                }
                if(i==2){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        PickVideoFromGallery();

                    }
                }

            }
        });
        builder.create().show();
    }

    private void PickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }
    private void PickVideoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*"); // Set the MIME type to video
        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE);
    }

    private void PickFromCamera() {
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent  intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }

    boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result;
    }
    void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return result && result1;
    }
    void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }


        return super.onOptionsItemSelected(item);
    }
    void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();


        if(user!=null){

            email=user.getEmail();
            uid=user.getUid();

        }else{
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean StorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && StorageAccepted){
                        PickFromCamera();
                    }else{
                        Toast.makeText(this, "Camera & Storage Permission are necessary..", Toast.LENGTH_SHORT).show();
                    }

                }else{

                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        PickFromGallery();
                    }else{
                        Toast.makeText(this, "Storage Permission necessary..", Toast.LENGTH_SHORT).show();

                    }
                }
            }break;
        }

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                imageView.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                imageView.setImageURI(image_uri);
            }
            else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                // User selected a video from the gallery
                videoUri = data.getData();
                // Handle the selected video (you can add your video upload logic here)
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}