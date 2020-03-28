package com.project.firebasesocial;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.firebasesocial.adapters.AdapterPosts;
import com.project.firebasesocial.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    ImageView avatarIv, coverIv;
    TextView nameTv, emailTv, phoneTv;
    RecyclerView postsRecyclerView;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        avatarIv = findViewById(R.id.avatarIv);
        coverIv = findViewById(R.id.coverIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        phoneTv = findViewById(R.id.phoneTv);
        postsRecyclerView = findViewById(R.id.recyclerview_posts);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
                    String phone = "" + ds.child("phone").getValue();
                    String image = "" + ds.child("image").getValue();
                    String cover = "" + ds.child("cover").getValue();

                    nameTv.setText(name);
                    emailTv.setText(email);
                    phoneTv.setText(phone);

                    try {
                        // if image is received then set
                        Picasso.get().load(image).into(avatarIv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                    try {
                        // if image is received then set
                        Picasso.get().load(cover).into(coverIv);
                    } catch (Exception e) {
                        //if there is any exception while getting image then set default
                        Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postList = new ArrayList<>();

        checkUserStatus();
        loadHisPosts();
    }

    private void loadHisPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ThereProfileActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);
                    //add to list
                    postList.add(myPosts);
                    //adapter
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHisPosts(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(ThereProfileActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recyclerview
        postsRecyclerView.setLayoutManager(layoutManager);

        //init posts list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    if (myPosts.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myPosts.getpDesc().toLowerCase().contains(searchQuery.toLowerCase())) {
                        //add to list
                        postList.add(myPosts);
                    }

                    //adapter
                    adapterPosts = new AdapterPosts(ThereProfileActivity.this, postList);
                    //set this adapter to recyclerview
                    postsRecyclerView.setAdapter(adapterPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_add_post).setVisible(false); //hide add post from this activity

        MenuItem item = menu.findItem(R.id.action_Search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button
                if (!TextUtils.isEmpty(s)) {
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user type any letter
                if (!TextUtils.isEmpty(s)) {
                    searchHisPosts(s);
                } else {
                    loadHisPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if (id == R.id.action_add_post) {
            startActivity(new Intent(ThereProfileActivity.this, AddPostActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
