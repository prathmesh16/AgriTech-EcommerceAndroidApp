package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdaptorClasses.User;
import com.appsnipp.profiledesigns.CircleTransform;
import com.appsnipp.profiledesigns.NewChatActivity;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.checkout;
import com.appsnipp.profiledesigns.detailEquip;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class useradaptor extends RecyclerView.Adapter<useradaptor.userViewHolder>{
    private Context mCtx;
    private List<User> UserList;
    private OnItemClickListner mListner;
    SharedPreferences pref;
    public interface OnItemClickListner {
        void OnItemClick(int position);
    }

    void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public useradaptor(Context mCtx, List<User> UserList) {
        this.mCtx = mCtx;
        this.UserList = UserList;
    }

    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_user_adaptor, parent, false);
        return new userViewHolder(view, mListner);
    }


    @Override
    public void onBindViewHolder(@NonNull final userViewHolder holder, int position) {

        final User msg = UserList.get(position);
        pref = mCtx.getSharedPreferences("user_details",MODE_PRIVATE);

            holder.name.setText(msg.getName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mCtx.getApplicationContext(), NewChatActivity.class);
                    i.putExtra("ToName",msg.getName());
                    mCtx.startActivity(i);
                }
            });


            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            storageRef.child(msg.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    // Got the download URL for 'users/me/profile.png'

                    Picasso.get().load(uri).transform(new CircleTransform()).into(holder.imageView);
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertadd = new AlertDialog.Builder(mCtx);
                            LayoutInflater factory = LayoutInflater.from(mCtx);
                            final View newview = factory.inflate(R.layout.sample1, null);
                            ImageView imageView1;
                            imageView1=newview.findViewById(R.id.dialog_imageview);
                            Picasso.get().load(uri).into(imageView1);
                            alertadd.setView(newview);
                            alertadd.show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
    }

    public int getItemCount() {
        return UserList.size();
    }
    class userViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imageView;
        TextView name;
        userViewHolder(@NonNull View itemView, final OnItemClickListner listner) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listner!=null)
                    {
                        int position=getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION){
                            listner.OnItemClick(position);
                        }
                    }
                }
            });
            name=itemView.findViewById(R.id.text_view_name);
            imageView=itemView.findViewById(R.id.image);
            cardView=itemView.findViewById(R.id.cardviewuser);
        }

    }

}
