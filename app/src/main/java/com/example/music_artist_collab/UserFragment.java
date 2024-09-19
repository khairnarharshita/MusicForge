package com.example.music_artist_collab;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.music_artist_collab.adapters.AdapterUsers;
import com.example.music_artist_collab.models.ModelUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;
    FirebaseAuth firebaseAuth;
    AppCompatActivity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar); // Assuming you have a toolbar in your fragment layout
        activity.setSupportActionBar(toolbar);

        // Initialize the RecyclerView
        recyclerView = view.findViewById(R.id.Users_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the users list
        usersList = new ArrayList<>();

        // Initialize the adapter with an empty list
        adapterUsers = new AdapterUsers(getActivity(), usersList);
        recyclerView.setAdapter(adapterUsers);

        // Get user data
        getAllUsers();
        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    private void getAllUsers() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);


                    if (modelUsers != null && fUser != null) {
                        String uid = modelUsers.getUid();
                        if (uid != null && !uid.equals(fUser.getUid())) {
                            usersList.add(modelUsers);
                        }
                    }
                }

                // Notify the adapter that the data has changed
                adapterUsers.notifyDataSetChanged();

                // Log the number of users retrieved
                Log.d("UserFragment", "Number of users retrieved: " + usersList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error and log the error message
                Log.e("UserFragment", "Database error: " + error.getMessage());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUser(s);
                } else {
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUser(s);
                } else {
                    getAllUsers();
                }
                return false;
            }

            private void searchUser(String s) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ModelUsers> filteredUsers = new ArrayList<>(); // Create a new list for filtered users

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelUsers modelUsers = ds.getValue(ModelUsers.class);
                            if (modelUsers != null && fUser != null) {
                                String uid = modelUsers.getUid();
                                if (uid != null && !uid.equals(fUser.getUid())) {
                                    if (modelUsers.getName().toLowerCase().contains(s.toLowerCase()) ||
                                            modelUsers.getEmail().toLowerCase().contains(s.toLowerCase())) {
                                        filteredUsers.add(modelUsers); // Add filtered users to the new list
                                    }
                                }
                            }
                        }

                        usersList.clear(); // Clear the existing list
                        usersList.addAll(filteredUsers); // Add filtered users to the main list

                        // Notify the adapter that the data has changed
                        adapterUsers.notifyDataSetChanged();

                        // Log the number of users retrieved
                        Log.e("UserFragment", "Number of users retrieved: " + usersList.size());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the error and log the error message
                        Log.e("UserFragment", "Database error: " + error.getMessage());
                    }
                });
            }

        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
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
}
