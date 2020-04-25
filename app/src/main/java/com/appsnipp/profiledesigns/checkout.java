package com.appsnipp.profiledesigns;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.AdaptorClasses.OrderAddress;
import com.appsnipp.profiledesigns.AdaptorClasses.OrderStatus;
import com.appsnipp.profiledesigns.AdaptorClasses.TimeLineModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class checkout extends AppCompatActivity {
    ProgressBar progress;
    Equp equp;
    OrderAddress orderAddress;
    EditText apname;
    EditText arname;
    EditText city;
    EditText postal;
    EditText dname;
    EditText phone;
   // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        progress=findViewById(R.id.progrees1);
        progress.setVisibility(View.VISIBLE);
        final ImageView imageView=(ImageView)findViewById(R.id.image);
        final TextView name=(TextView)findViewById(R.id.text_view_name);
        final TextView price=(TextView)findViewById(R.id.text_view_price);
        final TextView catagory=(TextView)findViewById(R.id.text_view_cat);
        apname=(EditText)findViewById(R.id.apname);
        arname=(EditText)findViewById(R.id.arname);
        city=(EditText)findViewById(R.id.cname);
        postal=(EditText)findViewById(R.id.postalcode);
        dname=(EditText)findViewById(R.id.dname);
        phone=(EditText)findViewById(R.id.Phone);

         orderAddress= new OrderAddress();

        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("PName"));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                equp=dataSnapshot.getValue(Equp.class);
                name.setText(equp.getName());
                price.setText("₹ "+equp.getPrice());
                catagory.setText(equp.getCatagory());
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                storageRef.child(equp.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        progress.setVisibility(View.INVISIBLE);
                        Picasso.get().load(uri).into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onlpay(View view)
    {
        orderAddress.setAddress(apname.getText().toString()+","+arname.getText().toString()+","+city.getText().toString()+","+dname.getText().toString());
        orderAddress.setPostalCode(postal.getText().toString());
        orderAddress.setPhone(phone.getText().toString());
        Intent intent=new Intent(checkout.this,checksum.class);
        intent.putExtra("name",equp.getName());
        intent.putExtra("seller",equp.getSeller());
        intent.putExtra("image",equp.getImage());
        intent.putExtra("catagory",equp.getCatagory());
        intent.putExtra("price",equp.getPrice());
        intent.putExtra("orderaddress", (Serializable) orderAddress);
        startActivity(intent);
    }
    public void cod(View view)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        String ordid=genOrderId();
                        orderAddress.setAddress(apname.getText().toString()+","+arname.getText().toString()+","+city.getText().toString()+","+dname.getText().toString());
                        orderAddress.setPostalCode(postal.getText().toString());
                        orderAddress.setPhone(phone.getText().toString());
                        SharedPreferences pref;
                        pref = getSharedPreferences("user_details",MODE_PRIVATE);
                        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference();
                        DatabaseReference db=ref1.child("Users").child(pref.getString("UName","")).child("orders").child(ordid);
                        db.child("price").setValue(equp.getPrice());
                        db.child("name").setValue(equp.getName());
                        db.child("catagory").setValue(equp.getCatagory());
                        db.child("seller").setValue(equp.getSeller());
                        db.child("customer").setValue(pref.getString("UName",""));
                        db.child("image").setValue(equp.getImage());
                        db.child("Delivery").setValue("Pending");
                        db.child("payment").setValue("Pending");
                        db.child("orderaddress").setValue(orderAddress);
                        db.child("ordid").setValue(ordid);
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();

                        DatabaseReference dbn=db.child("orderstatus").child("Stage1");
                        dbn.child("messege").setValue("Order placed successfully" );
                        dbn.child("date").setValue(dtf.format(now));
                        dbn.child("orderStatus").setValue("COMPLETED");
                        dbn.child("orderStatusno").setValue(1);

                        DatabaseReference dbn1=db.child("orderstatus").child("Stage2");
                        dbn1.child("messege").setValue("Order Confirming");
                        dbn1.child("date").setValue(dtf.format(now));
                        dbn1.child("orderStatus").setValue("ACTIVE");
                        dbn1.child("orderStatusno").setValue(2);
                        DatabaseReference dbn4=db.child("orderstatus").child("Stage3");
                        dbn4.child("messege").setValue("Order Shipped" );
                        dbn4.child("date").setValue(dtf.format(now));
                        dbn4.child("orderStatus").setValue("INACTIVE");
                        dbn4.child("orderStatusno").setValue(3);
                        DatabaseReference dbn2=db.child("orderstatus").child("Stage4");
                        dbn2.child("messege").setValue("Order out for delivery");
                        dbn2.child("date").setValue(dtf.format(now));
                        dbn2.child("orderStatus").setValue("INACTIVE");
                        dbn2.child("orderStatusno").setValue(4);
                        DatabaseReference dbn3=db.child("orderstatus").child("Stage5");
                        dbn3.child("messege").setValue("Order Successfully Deliverd");
                        dbn3.child("date").setValue(dtf.format(now));
                        dbn3.child("orderStatus").setValue("INACTIVE");
                        dbn3.child("orderStatusno").setValue(5);


                        DatabaseReference ref12 = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference db12 = ref12.child("Users").child(equp.getSeller()).child("delivery").child(ordid);
                        db12.child("price").setValue(equp.getPrice());
                        db12.child("name").setValue(equp.getName());
                        db12.child("catagory").setValue(equp.getCatagory());
                        db12.child("customer").setValue(pref.getString("UName",""));
                        db12.child("seller").setValue(equp.getSeller());
                        db12.child("image").setValue(equp.getImage());
                        db12.child("Delivery").setValue("Pending");
                        db12.child("payment").setValue("Pending");
                        db12.child("orderaddress").setValue(orderAddress);
                        db12.child("ordid").setValue(ordid);
                        DatabaseReference dbn12 = db12.child("orderstatus").child("Stage1");
                        dbn12.child("messege").setValue("Order placed successfully");
                        dbn12.child("date").setValue(dtf.format(now));
                        dbn12.child("orderStatus").setValue("COMPLETED");
                        dbn12.child("orderStatusno").setValue(1);

                        DatabaseReference dbn112 = db12.child("orderstatus").child("Stage2");
                        dbn112.child("messege").setValue("Order Confirming");
                        dbn112.child("date").setValue(dtf.format(now));
                        dbn112.child("orderStatus").setValue("ACTIVE");
                        dbn112.child("orderStatusno").setValue(2);
                        DatabaseReference dbn412 = db12.child("orderstatus").child("Stage3");
                        dbn412.child("messege").setValue("Order Shipped");
                        dbn412.child("date").setValue(dtf.format(now));
                        dbn412.child("orderStatus").setValue("INACTIVE");
                        dbn412.child("orderStatusno").setValue(3);
                        DatabaseReference dbn212 = db12.child("orderstatus").child("Stage4");
                        dbn212.child("messege").setValue("Order out for delivery");
                        dbn212.child("date").setValue(dtf.format(now));
                        dbn212.child("orderStatus").setValue("INACTIVE");
                        dbn212.child("orderStatusno").setValue(4);
                        DatabaseReference dbn312 = db12.child("orderstatus").child("Stage5");
                        dbn312.child("messege").setValue("Order Successfully Deliverd");
                        dbn312.child("date").setValue(dtf.format(now));
                        dbn312.child("orderStatus").setValue("INACTIVE");
                        dbn312.child("orderStatusno").setValue(5);



                        Intent i=new Intent(checkout.this,orderPlacement.class);
                        startActivity(i);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to Order (cod)?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }
    public String genOrderId() {
        Random r = new Random(System.currentTimeMillis());
        return "ORDER" + (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }
}
