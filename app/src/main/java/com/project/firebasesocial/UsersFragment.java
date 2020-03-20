package com.project.firebasesocial;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.firebasesocial.adapters.AdapterUsers;
import com.project.firebasesocial.models.ModelUser;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> usersList;

    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        //init recyclerView
        recyclerView = view.findViewById(R.id.user_recyclerView);
        //set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //init user list
        usersList = new ArrayList<>();

        //getAll users
        getAllUsers();


        return view;
    }

    private void getAllUsers() {
        // Get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        usersList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), usersList);

                    // set Adapter to recyclerView
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void searchUser(final String query) {
        // Get current user
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing user info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usersList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    //Get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        //can find by by username and email
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                        modelUser.getEmail().toLowerCase().contains(query.toLowerCase())){
                            usersList.add(modelUser);
                        }


                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), usersList);

                    //refresh adaptor
                    adapterUsers.notifyDataSetChanged();
                    // set Adapter to recyclerView
                    recyclerView.setAdapter(adapterUsers);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

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
        //Searchview
        MenuItem item = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listner
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // called when user press search button from keyboard
                //if search query not empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUser(s);
                }
                else{
                    //search text empty, get all users
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                // called when user press any single letter
                //if search query not empty then search
                if(!TextUtils.isEmpty(s.trim())){
                    //search text contains text, search it
                    searchUser(s);
                }
                else{
                    //search text empty, get all users
                    getAllUsers();
                }

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

}
