package com.example.music_artist_collab;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewProfile extends AppCompatActivity {
    private ImageView profileImage;
    private TextView usernameTextView;
    private TextView emailTextView;
    private VideoView videoView;
    // Add more TextViews or UI elements for other user information fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage);
        usernameTextView = findViewById(R.id.usernameTextView);
        emailTextView = findViewById(R.id.emailTextView);
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.suhani_flute)); // Set your video file in the res/raw directory


        // Initialize other UI elements for additional user information

        // Retrieve the user's ID from the Intent extras
        String userId = getIntent().getStringExtra("userId");

        // Get a reference to the user's data in the Firebase database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Add a ValueEventListener to fetch user data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    usernameTextView.setText(userName);
                    emailTextView.setText(userEmail);
                           if (dataSnapshot.hasChild("image")) {
                        String profileImageUrl = dataSnapshot.child("image").getValue(String.class);
                        Picasso.get().load(profileImageUrl).into(profileImage);
                    }

                    videoView.start();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that occur while fetching user data
            }
        });
    }
}
