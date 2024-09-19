package com.example.music_artist_collab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
Button MregisteBtn,mloginBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MregisteBtn=findViewById(R.id.register_btn);
        mloginBtn=findViewById(R.id.login_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish(); // Close the current activity to prevent going back to it.
        }

        MregisteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
   //set register activity
                Intent intent = new Intent(getApplicationContext(),RgisterActivity.class);
                startActivity(intent);

                      }
        });


        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set register activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}