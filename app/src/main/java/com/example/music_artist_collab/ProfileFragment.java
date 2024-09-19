package com.example.music_artist_collab;

import static android.app.Activity.RESULT_OK;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import android.widget.Toast;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ProfileFragment extends Fragment {

    Button logoutButton;
    FirebaseAuth firebaseAuth;
    ImageView avatarIv,coverIv;
    FirebaseUser user;
    TextView nameTv,emailTv,phoneTv;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FloatingActionButton fab;
    ProgressDialog progressDialog;
    Uri image_uri;
    String profileOrCoverPhoto;
    StorageReference storageReference;
    String storagepath="Users_Profile_Cover_Imgs/";
    AppCompatActivity activity;

FirebaseStorage getReference;
    String cameraPermission[];
    String storagePermission[];

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_GALLERY_CODE=300;
    private static final int IMAGE_PICK_CAMERA_CODE=400;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
//        actionBar.setTitle("Profile");
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("users");

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar); // Assuming you have a toolbar in your fragment layout
        activity.setSupportActionBar(toolbar);


        avatarIv=view.findViewById(R.id.avatarIv);
        nameTv=view.findViewById(R.id.name);
        emailTv=view.findViewById(R.id.email);
        phoneTv=view.findViewById(R.id.phone);
        coverIv=view.findViewById(R.id.coverIv);
        fab=view.findViewById(R.id.fab);
        progressDialog=new ProgressDialog(getActivity());
//        storageReference=getInstance().getReg
        storageReference = FirebaseStorage.getInstance().getReference();
        cameraPermission=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

logoutButton=view.findViewById(R.id.logoutButton);
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email= "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image= "" + ds.child("image").getValue();
                    String cover= "" + ds.child("cover").getValue();
                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);

                    try {
                        Picasso.get().load(image).into(avatarIv);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Picasso.get().load(R.drawable.ic_default_image_white).into(avatarIv);
                    }

                    try {
                        Picasso.get().load(image).into(coverIv);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Picasso.get().load(R.drawable.ic_default_image_white).into(coverIv);
                    }

                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });
        return view ;
    }

    private void showEditProfileDialog() {

        String options[]={"edit profile pic","edit cover photo","edit phone","edit Name"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    progressDialog.setMessage("Updating Profile pic");
                    profileOrCoverPhoto="image";
                    showImagePicDialog();

                } else if(i==1){
                    progressDialog.setMessage("Updating cover pic");
                    profileOrCoverPhoto="Cover";
                    showImagePicDialog();
                }else if(i==2){
                    progressDialog.setMessage("Updating phone number");
                    showNamePhoneUpdateDialog("phone");
                }else if(i==3){
                    progressDialog.setMessage("Updating name");
                    showNamePhoneUpdateDialog("name");
                }
            }
        });
        builder.create().show();


    }

    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update"+key);
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        EditText editText=new EditText(getActivity());
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);


        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value=editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    progressDialog.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,value);
                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),"Updated..",Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                }
                            });

                }else{
                    Toast.makeText(getActivity(),"Please Enter Key"+key,Toast.LENGTH_SHORT).show();

                }
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();

    }

    private void showImagePicDialog() {
        String options[]={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
//                    progressDialog.setMessage("Updating Profile pic");
//                    showImagePicDialog();
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                } else if(i==1){
//                    progressDialog.setMessage("Updating cover pic");
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch(requestCode){
           case CAMERA_REQUEST_CODE:{
               if(grantResults.length>0) {
                   boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                   boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                   if (cameraAccepted && writeStorageAccepted) {
                       pickFromCamera();
                   } else {
                       Toast.makeText(getActivity(), "Please enable camera and storage permission ", Toast.LENGTH_SHORT);
                   }
               }
           }
           break;
           case STORAGE_REQUEST_CODE:{


               if(grantResults.length>0) {
                   boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                   if (writeStorageAccepted) {
                       pickFromGallery();
                   }
                   else{
                       Toast.makeText(getActivity(), "Please enable storage permission ", Toast.LENGTH_SHORT);
                   }
               }
           }break;

       }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if(resultCode==RESULT_OK){
           if(requestCode==IMAGE_PICK_GALLERY_CODE){
               //image is picked up from galery
               image_uri=data.getData();
               uploadProfileCoverPhoto(image_uri);

           }
           if(requestCode==IMAGE_PICK_CAMERA_CODE){
               uploadProfileCoverPhoto(image_uri);

           }
       }

        super.onActivityResult(requestCode, resultCode, data);
    }
//    private void uploadProfileCoverPhoto(Uri uri) {
//        progressDialog.show();
//        String filePathAndName = storagepath + profileOrCoverPhoto + "_" + user.getUid(); // Remove spaces and use "_"
//        StorageReference storageReference2nd = storageReference.child(filePathAndName);
//
//        storageReference2nd.putFile(uri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get the download URL
//                        storageReference2nd.getDownloadUrl()
//                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri downloadUri) {
//                                        HashMap<String, Object> results = new HashMap<>();
//                                        results.put(profileOrCoverPhoto, downloadUri.toString());
//
//                                        // Check if it's a profile picture or a cover photo
//                                        if (profileOrCoverPhoto.equals("image")) {
//                                            // Update profile picture
//                                            databaseReference.child(user.getUid()).updateChildren(results)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void unused) {
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(getActivity(), "Profile Picture updated", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(getActivity(), "Error Updating Profile Picture", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        } else if (profileOrCoverPhoto.equals("Cover")) {
//                                            // Update cover photo
//                                            databaseReference.child(user.getUid()).updateChildren(results)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void unused) {
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(getActivity(), "Cover Photo updated", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
//                                                            progressDialog.dismiss();
//                                                            Toast.makeText(getActivity(), "Error Updating Cover Photo", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//                                        }
//                                    }
//                                });
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
private void uploadProfileCoverPhoto(Uri uri) {
    progressDialog.show();
    String filePathAndName = storagepath + profileOrCoverPhoto + "_" + user.getUid();
    StorageReference storageReference2nd = storageReference.child(filePathAndName);

    storageReference2nd.putFile(uri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference2nd.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    HashMap<String, Object> results = new HashMap<>();
                                    results.put(profileOrCoverPhoto, downloadUri.toString());

                                    // Check if it's a profile picture or a cover photo
                                    if (profileOrCoverPhoto.equals("image")) {
                                        // Update profile picture
                                        databaseReference.child(user.getUid()).child("image").setValue(downloadUri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Profile Picture updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Error Updating Profile Picture",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else if (profileOrCoverPhoto.equals("cover")) {
                                        // Update cover photo
                                        databaseReference.child(user.getUid()).child("cover").setValue(downloadUri.toString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Cover Photo updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), "Error Updating Cover Photo", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
}


    boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }
    void requestStoragePermission(){
        requestPermissions(storagePermission,STORAGE_REQUEST_CODE);
    }

    boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);


        boolean result1= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    void requestCameraPermission(){
       requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }
    private void pickFromCamera() {
        ContentValues values= new ContentValues();
                values.put(MediaStore.Images.Media.TITLE,"Temp pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);



    }

    private void pickFromGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE);
    }

    void checkUserStatus(){
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){

        }else{
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();

        }
        if(id==R.id.action_add_post){
            startActivity(new Intent(getActivity(),AddPostActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}