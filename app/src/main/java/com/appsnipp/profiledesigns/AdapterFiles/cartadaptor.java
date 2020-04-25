package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.appsnipp.profiledesigns.ui.myItem.myItemFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class cartadaptor extends RecyclerView.Adapter<cartadaptor.cartViewHolder>{
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

    public cartadaptor(Context mCtx, List<Equp> EqupList) {
        this.mCtx = mCtx;
        this.EqupList = EqupList;
    }

    @NonNull
    @Override
    public cartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_cartadaptor, parent, false);
        return new cartViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final cartViewHolder holder, int position) {
        final Equp msg = EqupList.get(position);
        pref = mCtx.getSharedPreferences("user_details",MODE_PRIVATE);



            holder.textViewName.setText(msg.getName());
            holder.textViewPrice.setText("₹ "+msg.getPrice());
            holder.textViewCat.setText(msg.getCatagory());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(mCtx.getApplicationContext(), detailEquip.class);
                    i.putExtra("PName",msg.getName());
                    mCtx.startActivity(i);
                }
            });

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
                                    dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(pref.getString("UName","")).child("cart").child(msg.getName());
                                    dbUsers.removeValue();

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

                }
            });

        holder.checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbUsers;
                dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(pref.getString("UName","")).child("cart").child(msg.getName());
                dbUsers.removeValue();
                Intent i= new Intent(mCtx.getApplicationContext(), checkout.class);
                i.putExtra("PName",msg.getName());
                mCtx.startActivity(i);
            }
        });


    }

    public int getItemCount() {
        return EqupList.size();
    }
    class cartViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName,textViewPrice,textViewCat;
        ImageView image,checkout;
        ImageButton deleteItem;
        ProgressBar progressBar;
        CardView cardView;
        cartViewHolder(@NonNull View itemView, final OnItemClickListner listner) {
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
            cardView=itemView.findViewById(R.id.cardviewcart);
            checkout=itemView.findViewById(R.id.imageView);
            image=itemView.findViewById(R.id.image);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice=itemView.findViewById(R.id.text_view_price);
            textViewCat=itemView.findViewById(R.id.text_view_cat);
            progressBar=itemView.findViewById(R.id.progrees1);
            deleteItem=(ImageButton)itemView.findViewById(R.id.deleteItem);
        }

    }
}
