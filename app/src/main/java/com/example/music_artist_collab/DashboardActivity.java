package com.example.music_artist_collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.music_artist_collab.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    FrameLayout framelayout;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.navigation); // Use the correct ID
        framelayout = findViewById(R.id.content);

        HomeFragment fragment1 = new HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();
        checkUserStatus();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    HomeFragment f1 = new HomeFragment();
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                    ft1.replace(R.id.content, f1, " ");
                    ft1.commit();
                    return true;
                } else if (itemId == R.id.nav_users) {
                    UserFragment uf = new UserFragment();
                    FragmentTransaction uf1 = getSupportFragmentManager().beginTransaction();
                    uf1.replace(R.id.content, uf, " ");
                    uf1.commit();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    ProfileFragment pf = new ProfileFragment();
                    FragmentTransaction pf1 = getSupportFragmentManager().beginTransaction();
                    pf1.replace(R.id.content, pf, " ");
                    pf1.commit();
                    return true;
                } else if (itemId == R.id.chat) {
                    ChatListFragment cf = new ChatListFragment();
                    FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                    ft4.replace(R.id.content, cf, " ");
                    ft4.commit();

                    return true;
                }

                return false;
            }
        });
        updateToken(String.valueOf(FirebaseMessaging.getInstance().getToken()));
    }

    public void updateToken(String token) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken = new Token(token);
        ref.child(mUID).setValue(mtoken);
    }

    void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            mUID = user.getUid();
            SharedPreferences sp = getSharedPreferences("sp_user", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        checkUserStatus();
    }

    void OnResume() {
        checkUserStatus();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
