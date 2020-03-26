package com.project.firebasesocial.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.$Gson$Preconditions;
import com.project.firebasesocial.ChatActivity;
import com.project.firebasesocial.R;
import com.project.firebasesocial.ThereProfileActivity;
import com.project.firebasesocial.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> usersList;

    FirebaseAuth firebaseAuth;
    String myUid;
    //Constructor

    public AdapterUsers(Context context, List<ModelUser> usersList){
        this.context = context;
        this.usersList = usersList;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid = firebaseAuth.getUid();
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layout(row_user.xml)
        // com.google.firebase.database.core.view.View
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup, false );

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) {
        //Get Data
        final String hisUid = usersList.get(i).getUid();
        String userImage = usersList.get(i).getImage();
        final String userName = usersList.get(i).getName();
        final String userEmail = usersList.get(i).getEmail();

        //Set Data
        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        try{

            Picasso.get().load(userImage).
                    placeholder(R.drawable.ic_default_img).
                    into(myHolder.mAvatarIv);
        }
        catch(Exception e){

        }

        myHolder.blockIv.setImageResource(R.drawable.ic_unblocked_green);
        checkIsBlocked(hisUid, myHolder, i);
        //handle item exception
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            //profile clicked
                            Intent intent = new Intent(context, ThereProfileActivity.class);
                            intent.putExtra("uid", hisUid);
                            context.startActivity(intent);
                        }
                        if (which == 1){
                            //chat clicked
                            imBlockedORNot(hisUid);
                        }
                    }
                });
                builder.create().show();
            }
        });


        myHolder.blockIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersList.get(i).isBlocked()){
                        unBlockUser(hisUid);
                }else {
                    blockedUser(hisUid);
                }
            }
        });

    }
    private void imBlockedORNot(final String hisUid){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("BlockedUsers").orderByChild("uid").equalTo(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            if(ds.exists()){
                                Toast.makeText(context, "You 're blocked by that user, can't send message", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisUid", hisUid);
                        context.startActivity(intent);
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void checkIsBlocked(String hisUid, final MyHolder myHolder, final int i) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("BlockedUsers").orderByChild("uid").equalTo(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds:dataSnapshot.getChildren()){
                            if(ds.exists()){
                                myHolder.blockIv.setImageResource(R.drawable.ic_blocked_red);
                                usersList.get(i).setBlocked(true);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void blockedUser(String hisUid) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("uid", hisUid);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").child(hisUid).setValue(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Blocked Successfully...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed: "+e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unBlockUser(String hisUid) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUsers").orderByChild("uid").equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            if(ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Unblocked Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed: "+e.getMessage() , Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //View holder class
     class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv, blockIv;
        TextView mNameTv, mEmailTv;

         public MyHolder(@NonNull View itemView) {
            super(itemView);

             //init view
             mAvatarIv = itemView.findViewById(R.id.avatarIv);
             blockIv = itemView.findViewById(R.id.blockIv);
             mNameTv = itemView.findViewById(R.id.nameTv);
             mEmailTv = itemView.findViewById(R.id.emailTv);

        }

    }
}
