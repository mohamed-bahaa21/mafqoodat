package com.project.firebasesocial.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.ValueEventRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.firebasesocial.AddPostActivity;
import com.project.firebasesocial.R;
import com.project.firebasesocial.ThereProfileActivity;
import com.project.firebasesocial.models.ModelPost;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;

    String myUid;

    private DatabaseReference likesRef;
    private DatabaseReference postRef;

    boolean mProcessLike=false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        final String uid = postList.get(i).getUid();
        String email = postList.get(i).getEmail();
        String name = postList.get(i).getName();
        String pDesc = postList.get(i).getpDesc();
        final String pId = postList.get(i).getpId();
        final String pImage = postList.get(i).getpImage();
        String pTimestamp = postList.get(i).getpTime();
        String pTitle = postList.get(i).getpTitle();
        String uDp = postList.get(i).getuDp();
        String pLikes = postList.get(i).getpLikes(); //Contain number of likes for a post

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String pTime = DateFormat.format("dd/MM/yyy hh:mm aa", calendar).toString();

        //set data
        myHolder.uNameTv.setText(name);
        myHolder.pTimeTv.setText(pTime);
        myHolder.pTitleTv.setText(pTitle);
        myHolder.pDescriptionTv.setText(pDesc);
        myHolder.pLikesTv.setText(pLikes + "Likes"); //e.g 100 Likes

        setLike(myHolder, pId);

        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_img).into(myHolder.uPictureIv);
        }catch (Exception e){

        }

        if (pImage.equals("noImage")){
            //hide imageview
            myHolder.pImageIv.setVisibility(View.GONE);
        }else{
            //show imageview
            myHolder.pImageIv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(myHolder.pImageIv);
            }catch (Exception e){

            }
        }

        myHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                showMoreOptions(myHolder.moreBtn, uid, myUid, pId, pImage);
            }
        });

        myHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });
        myHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will implement later
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        myHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes = Integer.parseInt(postList.get(i).getpLikes());
                mProcessLike = true;
                //get id of the post clicked
                final String postIde = postList.get(i).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ( mProcessLike ){
                            if ( dataSnapshot.child(postIde).hasChild(myUid)){
                                //already liked so remove likes
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                                likesRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }else{
                                //not Liked, Like it
                                postRef.child(postIde).child("pLike").setValue(""+(pLikes+1));
                                likesRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        myHolder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid", uid);
                context.startActivity(intent);
            }
        });
    }

    private void setLike(final MyHolder holder, final String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postKey).hasChild(myUid)){
                    // User has Liked this post

                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked  , 0,0,0);
                    holder.likeBtn.setText("Liked");

                }else{
                    // User has not Liked this post

                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black  , 0,0,0);
                    holder.likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId, final String pImage) {
        final PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        //show delete option in only post(s) of currently signed-in user
        if (uid.equals(myUid)){
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == 0){
                    //delete is clicked
                    beginDelete(pId, pImage);
                } else if (id == 1){
                    //edit is clicked
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        //post can be with or without image
        if (pImage.equals("noImage")){
            //post is without image
            deleteWithoutImage(pId);
        } else {
            //post is with image
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();

        //1) delete image using url
        //2) delete from database usin post id

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, now delete database
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                                }
                                //deleted
                                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed cant go further
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String pId) {
        //progress bar
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        pd.show();

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue(); //remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView uPictureIv, pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;

        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);
        }
    }
}
