package com.project.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv, userStatusTv;
    EditText messageEt;
    ImageButton sendBtn;



    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbRef;

    String hisUid;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        recyclerView = findViewById(R.id.chat_recyclerView);
        profileIv = findViewById(R.id.profileIV);
        nameTv = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);



        Intent intent = getIntent();
        hisUid =intent.getStringExtra("hisUid");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbRef = firebaseDatabase.getReference("Users");

        Query userQuery = usersDbRef.orderByChild("uid").equalTo(hisUid);

        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    String name =""+ ds.child("name").getValue();
                    String image =""+ ds.child("image").getValue();

                    nameTv.setText(name);
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.ic_default_img_white).into(profileIv);

                    }catch (Exception e){

                        Picasso.get().load(R.drawable.ic_default_img_white).into(profileIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage (message);
                    
                }
            }
        });
    }

    private void sendMessage(String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myUid);
        hashMap.put("receiver", hisUid);
        hashMap.put("message", message);
        databaseReference.child("Chats").push().setValue(hashMap);

        messageEt.setText("");
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();


        if(user != null){

            myUid = user.getUid();

        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //hide search view as we don't need it
        menu.findItem(R.id.action_Search).setVisible(false);

        return super.onCreateOptionsMenu(menu);
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
