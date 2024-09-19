package com.example.music_artist_collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    EditText mEmailEt, mPasswordEt;
    TextView not_have_account_tv, mrecover;
    Button mLoginBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginBtn = findViewById(R.id.loginbtn);
        not_have_account_tv = findViewById(R.id.not_have_account_tv);
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mrecover = findViewById(R.id.recover);
        mAuth = FirebaseAuth.getInstance();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    mPasswordEt.setError("Password length must be at least 6 char");
                    mPasswordEt.setFocusable(true);
                } else {
                    LoginUser(email, password);
                }

            }
        });
        not_have_account_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RgisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void LoginUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            FirebaseUser user = mAuth.getCurrentUser();


                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {


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


                            }
                            Toast.makeText(LoginActivity.this, "Login.",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        mrecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecoverPasswordDialoue();
            }
        });
    }

    private void showRecoverPasswordDialoue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("recover password");
        LinearLayout linearLayout = new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String emailEt = mEmailEt.getText().toString().trim();
                beginRecovery(emailEt);
            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String emailEt) {
        mAuth.sendPasswordResetEmail(emailEt).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, " Email Sent.",
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(LoginActivity.this, "failed.",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, " " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

}