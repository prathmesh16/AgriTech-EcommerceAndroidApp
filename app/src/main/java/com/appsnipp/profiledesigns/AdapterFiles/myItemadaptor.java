package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.detailEquip;
import com.appsnipp.profiledesigns.ui.myItem.myItemFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class myItemadaptor extends RecyclerView.Adapter<myItemadaptor.EquViewHolder>{
    private Context mCtx;
    private List<Equp> EqupList;
    private OnItemClickListner mListner;
    public interface OnItemClickListner {
        void OnItemClick(int position);
    }

    void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public myItemadaptor(Context mCtx, List<Equp> EqupList) {
        this.mCtx = mCtx;
        this.EqupList = EqupList;
    }

    @NonNull
    @Override
    public EquViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_myitemadaptor, parent, false);
        return new EquViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final EquViewHolder holder, int position) {
        final Equp msg = EqupList.get(position);




            holder.textViewName.setText(msg.getName());
            holder.textViewPrice.setText("₹ "+msg.getPrice());
            holder.textViewCat.setText(msg.getCatagory());
        ((Activity) mCtx).getFragmentManager();//use this


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

            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    DatabaseReference dbUsers;
                                    dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(msg.getSeller()).child("MyItems").child(msg.getName());
                                    dbUsers.removeValue();

                                    DatabaseReference dbUsers1;
                                    dbUsers1 = FirebaseDatabase.getInstance().getReference("Posts").child(msg.getName());
                                    dbUsers1.removeValue();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                    builder.setMessage("Do you want to remove "+msg.getName()+"?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    myItemFragment myFragment = new myItemFragment();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, myFragment).addToBackStack(null).commit();

                }
            });




    }

    public int getItemCount() {
        return EqupList.size();
    }
    class EquViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName,textViewPrice,textViewCat;
        ImageView image;
        ImageButton deleteItem;
        ProgressBar progressBar;
        EquViewHolder(@NonNull View itemView, final OnItemClickListner listner) {
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
            image=itemView.findViewById(R.id.image);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice=itemView.findViewById(R.id.text_view_price);
            textViewCat=itemView.findViewById(R.id.text_view_cat);
            progressBar=itemView.findViewById(R.id.progrees1);
            deleteItem=(ImageButton)itemView.findViewById(R.id.deleteItem);
        }

    }
}
