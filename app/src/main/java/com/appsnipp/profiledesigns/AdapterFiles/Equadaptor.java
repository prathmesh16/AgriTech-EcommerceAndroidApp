package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.MainActivity;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.checkout;
import com.appsnipp.profiledesigns.checksum;
import com.appsnipp.profiledesigns.detailEquip;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Equadaptor  extends RecyclerView.Adapter<Equadaptor.EquViewHolder>{
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

    public Equadaptor(Context mCtx, List<Equp> EqupList) {
        this.mCtx = mCtx;
        this.EqupList = EqupList;
    }

    @NonNull
    @Override
    public EquViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_equadaptor, parent, false);
        return new EquViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final EquViewHolder holder, int position) {
            final Equp msg = EqupList.get(position);
            holder.textViewName.setText(msg.getName());
            holder.textViewPrice.setText("₹ "+msg.getPrice());
            holder.textViewCat.setText(msg.getCatagory());
            holder.ratingBar.setRating(msg.getRating().getAvgrating());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child(msg.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                holder.progressBar.setVisibility(View.INVISIBLE);
                Picasso.get().load(uri).into(holder.image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(mCtx.getApplicationContext(), detailEquip.class);
                i.putExtra("PName",msg.getName());
                mCtx.startActivity(i);
            }
        });
        holder.addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                pref = mCtx.getSharedPreferences("user_details",MODE_PRIVATE);
                                DatabaseReference ref1= FirebaseDatabase.getInstance().getReference();
                                DatabaseReference db=ref1.child("Users").child(pref.getString("UName","")).child("cart").child(msg.getName());
                                db.child("price").setValue(msg.getPrice());
                                db.child("name").setValue(msg.getName());
                                db.child("catagory").setValue(msg.getCatagory());
                                db.child("seller").setValue(msg.getSeller());
                                db.child("image").setValue(msg.getImage());
                                Toast.makeText(mCtx.getApplicationContext(),"Item added to cart !!",Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setMessage("Do you want to add "+msg.getName()+" in cart?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        holder.buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Intent i=new Intent(mCtx.getApplicationContext(), checkout.class);
                                i.putExtra("PName",msg.getName());
                                mCtx.startActivity(i);
                                 break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
                builder.setMessage("Do you want to buy "+msg.getName()+" now?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                 }
        });


    }

    public int getItemCount() {
        return EqupList.size();
    }
    class EquViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName,textViewPrice,textViewCat;
        ImageView image,addtocart,buynow;
        ShimmerFrameLayout progressBar;
        CardView cardView;
        RatingBar ratingBar;
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
            ratingBar=itemView.findViewById(R.id.ratingBar2);
            cardView=itemView.findViewById(R.id.card);
            image=itemView.findViewById(R.id.image);
            addtocart=itemView.findViewById(R.id.imageView2);
            buynow=itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewPrice=itemView.findViewById(R.id.text_view_price);
            textViewCat=itemView.findViewById(R.id.text_view_cat);
            progressBar=itemView.findViewById(R.id.progrees1);
        }

    }
}
