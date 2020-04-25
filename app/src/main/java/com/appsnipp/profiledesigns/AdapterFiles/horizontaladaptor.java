package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
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

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class horizontaladaptor extends RecyclerView.Adapter<horizontaladaptor.horiViewHolder>{
    private Context mCtx;
    private List<Equp> EqupList;
    private OnItemClickListner mListner;
    SharedPreferences pref;
    public interface OnItemClickListner {
        void OnItemClick(int position);
    }

    void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public horizontaladaptor(Context mCtx, List<Equp> EqupList) {
        this.mCtx = mCtx;
        this.EqupList = EqupList;
    }

    @NonNull
    @Override
    public horiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_horizontal_adaptor, parent, false);
        return new horiViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final horiViewHolder holder, int position) {
        final Equp msg = EqupList.get(position);
            pref = mCtx.getSharedPreferences("user_details",MODE_PRIVATE);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mCtx.getApplicationContext(), detailEquip.class);
                    i.putExtra("PName",msg.getName());
                    mCtx.startActivity(i);
                }
            });
            holder.name.setText(msg.getName());
            String roundedrating=String.format("%.1f",msg.getRating().getAvgrating());
            holder.rating.setText(""+roundedrating);
            DatabaseReference db1= FirebaseDatabase.getInstance().getReference().child("Posts");
            String key = db1.push().getKey();
            db1.child(key);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            storageRef.child(msg.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'

                    Picasso.get().load(uri).into(holder.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
    }

    public int getItemCount() {
        return EqupList.size();
    }
    class horiViewHolder extends RecyclerView.ViewHolder {

        TextView name,rating;
        ImageView image;
        ProgressBar progressBar;
        CardView cardView;
        horiViewHolder(@NonNull View itemView, final OnItemClickListner listner) {
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
            rating=itemView.findViewById(R.id.rating);
            cardView=itemView.findViewById(R.id.cardView2);
            progressBar=itemView.findViewById(R.id.progrees1);
            image=itemView.findViewById(R.id.imageView5);
            name = itemView.findViewById(R.id.textView2);
        }

    }
}
