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
import android.widget.Toast;

import com.example.music_artist_collab.adapters.AdapterPost;
import com.example.music_artist_collab.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    AppCompatActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar); // Assuming you have a toolbar in your fragment layout
        activity.setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.postRecycleView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);


        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        loadPosts();

        return view;
    }

    public void loadPosts() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    postList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        if (modelPost != null) {
                            // Check if the post has an image or video
                            Log.d("PostData", "Title: " + modelPost.getpTitle());
                            Log.d("PostData", "Description: " + modelPost.getpDescription());
                            Log.d("PostData", "Description: " + modelPost.getpVideo());

                            if (!TextUtils.isEmpty(modelPost.getpImage()) || !TextUtils.isEmpty(modelPost.getpVideo())) {
                                postList.add(modelPost);
                            }
                            adapterPost = new AdapterPost(getActivity(), postList);
                            recyclerView.setAdapter(adapterPost);
                        }
                    }

                    adapterPost.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error in onDataChange: " + e.getMessage());
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error loading posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void searchPost(String searchQuery) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ModelPost> filteredPost = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if (modelPost != null) {
                        String pTitle = modelPost.getpTitle();
                        String pDescr = modelPost.getpDescription();
                        String username = modelPost.getuName();
                        if (pTitle != null) {
                            if (pTitle.toLowerCase().contains(searchQuery.toLowerCase()) || username.toLowerCase().contains(searchQuery.toLowerCase())) {
                                filteredPost.add(modelPost);
                            }
                        }
                    }

                }

                postList.clear(); // Clear the existing list
                postList.addAll(filteredPost); // Add filtered posts to the main list

                // Notify the adapter that the data has changed
                adapterPost.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        android.widget.SearchView searchView = (android.widget.SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchPost(query);
                } else {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (!TextUtils.isEmpty(newText)) {
                    searchPost(newText);
                } else {
                    loadPosts();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();

        }
        if (id == R.id.action_add_post) {
            startActivity(new Intent(getActivity(), AddPostActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}