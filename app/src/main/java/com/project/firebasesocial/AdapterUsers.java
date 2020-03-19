package com.project.firebasesocial;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.LayoutInflaterFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUsers> usersList;

    //Constructor

    public AdapterUsers(Context context, List<ModelUsers> usersList){
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

                Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
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
