package com.project.firebasesocial;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.firebasesocial.adapters.AdapterPosts;
import com.project.firebasesocial.models.ModelPost;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPosts adapterPosts;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.postsRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        loadPosts();

        return view;
    }

    private void loadPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(final String searchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    if (modelPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelPost.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(modelPost);
                    }
                    adapterPosts = new AdapterPosts(getActivity(), postList);
                    recyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //Called when user click search button
                if (!TextUtils.isEmpty(s)) {
                    searchPosts(s);
                } else {
                    loadPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called when user press any letter
                if (!TextUtils.isEmpty(s)) {
                    searchPosts(s);
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
        } else if (id == R.id.action_add_post) {
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        } else if (id == R.id.action_Settings) {

            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}


