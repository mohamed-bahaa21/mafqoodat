package com.project.firebasesocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.firebasesocial.ChatActivity;
import com.project.firebasesocial.R;
import com.project.firebasesocial.models.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUser> usersList;

    //Constructor

    public AdapterUsers(Context context, List<ModelUser> usersList){
        this.context = context;
        this.usersList = usersList;
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
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //Get Data
        final String hisUid = usersList.get(i).getUid();
        String userImage = usersList.get(i).getimage();
        String userName = usersList.get(i).getName();
        final String userEmail = usersList.get(i).getemail();

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
        //handle item exception
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //View holder class
     class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

         public MyHolder(@NonNull View itemView) {
            super(itemView);

             //init view
             mAvatarIv = itemView.findViewById(R.id.avatarIv);
             mNameTv = itemView.findViewById(R.id.nameTv);
             mEmailTv = itemView.findViewById(R.id.emailTv);

        }

    }
}
