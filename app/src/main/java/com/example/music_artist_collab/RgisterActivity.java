package com.example.music_artist_collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RgisterActivity extends AppCompatActivity {
    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn, backBtn;
    ProgressDialog pgdia;
    FirebaseAuth mAuth;
    TextView mHaveAcctv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgister);
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();
        mHaveAcctv = findViewById(R.id.have_account_tv);
        backBtn = findViewById(R.id.backBtn);
        mHaveAcctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    mPasswordEt.setError("Password length must be at least 6 char");
                    mPasswordEt.setFocusable(true);
                } else {
//                    registerUser(email,password);


                    mRegisterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                String email = user.getEmail();
                                                String uid = user.getUid();
                                                HashMap<Object, String> hashMap = new HashMap<>();
                                                hashMap.put("email", email);
                                                hashMap.put("uid", uid);
                                                hashMap.put("name", "");
                                                hashMap.put("onlineStatus", "online");
                                                hashMap.put("typingTo", "NoOne");
                                                hashMap.put("phone", "");
                                                hashMap.put("Image", "");
                                                hashMap.put("cover", "");

                                                FirebaseDatabase dataBase = FirebaseDatabase.getInstance();

                                                DatabaseReference reference = dataBase.getReference("users");
                                                reference.child(uid).setValue(hashMap);
                                                Toast.makeText(RgisterActivity.this, "Account Created.",
                                                        Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(RgisterActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}