package com.appsnipp.profiledesigns.AdapterFiles;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.appsnipp.profiledesigns.AdaptorClasses.Order;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.orderTracking;
import com.appsnipp.profiledesigns.ui.myItem.myItemFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class myorderadaptor extends RecyclerView.Adapter<myorderadaptor.orderViewHolder>{
    private Context mCtx;
    private List<Order> OrderList;
    private OnItemClickListner mListner;
    private String CType;
    public interface OnItemClickListner {
        void OnItemClick(int position);
    }

    void setOnItemClickListner(OnItemClickListner listner) {
        mListner = listner;
    }

    public myorderadaptor(Context mCtx, List<Order> OrderList,String CType) {
        this.mCtx = mCtx;
        this.OrderList = OrderList;
        this.CType=CType;
    }

    @NonNull
    @Override
    public orderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.activity_order_adaptor, parent, false);
        return new orderViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull final orderViewHolder holder, int position) {
        final Order msg = OrderList.get(position);



            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i=new Intent(mCtx.getApplicationContext(), orderTracking.class);
                    i.putExtra("PName",msg.getName());
                    i.putExtra("ordid",msg.getOrdid());
                    if (CType.equals("customer"))
                        i.putExtra("CType","Customer");
                    else
                    {
                        i.putExtra("CType","Seller");
                        i.putExtra("customer",msg.getCustomer());
                    }
                    mCtx.startActivity(i);
                }
            });
            holder.textViewName.setText(msg.getName());
            holder.textViewPrice.setText("₹ "+msg.getPrice());
            holder.textViewCat.setText(msg.getCatagory());
            holder.payment.setText("Payment Status:"+msg.getPayment());
            holder.delivery.setText("Delivery:"+msg.getDelivery());
        ((Activity) mCtx).getFragmentManager();//use this


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
        return OrderList.size();
    }
    class orderViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName,textViewPrice,textViewCat,delivery,payment;
        ImageView image;
        ProgressBar progressBar;
        CardView cardView;
        orderViewHolder(@NonNull View itemView, final OnItemClickListner listner) {
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
            cardView=itemView.findViewById(R.id.card);
            image=itemView.findViewById(R.id.image);
            textViewName = itemView.findViewById(R.id.text_view_name);
            delivery=itemView.findViewById(R.id.textView8);
            payment=itemView.findViewById(R.id.textView9);
            textViewPrice=itemView.findViewById(R.id.text_view_price);
            textViewCat=itemView.findViewById(R.id.text_view_cat);
            progressBar=itemView.findViewById(R.id.progrees1);
        }

    }
}
