package com.appsnipp.profiledesigns;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdapterFiles.horizontaladaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
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

import java.util.ArrayList;
import java.util.List;

public class detailEquip extends AppCompatActivity {
    ProgressBar progress;
    Equp equp;
    SharedPreferences pref;
    float rating=0;
    int noofpeople=0;

    private horizontaladaptor adapter1;
    private List<Equp> EqupList1;
    DatabaseReference dbUsers1;
    RecyclerView recyclerView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_equip);
        progress=findViewById(R.id.progrees1);
        progress.setVisibility(View.VISIBLE);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        final ImageView imageView=(ImageView)findViewById(R.id.image);
        final TextView name=(TextView)findViewById(R.id.text_view_name);
        final TextView price=(TextView)findViewById(R.id.text_view_price);
        final TextView catagory=(TextView)findViewById(R.id.text_view_cat);
        final TextView seller=(TextView)findViewById(R.id.seller);
        final TextView detail=(TextView)findViewById(R.id.Detail);
        ImageView buynow=(ImageView)findViewById(R.id.imageView) ;
        final RatingBar ratingBar=(RatingBar)findViewById(R.id.ratingBar);
        ratingBar.setStepSize(0.1f);
        final Button submitrating=(Button)findViewById(R.id.submitrating);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView1 =findViewById(R.id.horizontalrecyclerviewdetail);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        EqupList1 = new ArrayList<>();
        adapter1 = new horizontaladaptor(this, EqupList1);
        recyclerView1.setAdapter(adapter1);

        buynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getApplicationContext(), checkout.class);
                i.putExtra("PName",getIntent().getStringExtra("PName"));
                startActivity(i);
            }
        });
        ImageView addtocart=(ImageView)findViewById(R.id.imageView2);
        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                DatabaseReference ref1= FirebaseDatabase.getInstance().getReference();
                                DatabaseReference db=ref1.child("Users").child(pref.getString("UName","")).child("cart").child(equp.getName());
                                db.child("price").setValue(equp.getPrice());
                                db.child("name").setValue(equp.getName());
                                db.child("catagory").setValue(equp.getCatagory());
                                db.child("seller").setValue(equp.getSeller());
                                db.child("image").setValue(equp.getImage());
                                Toast.makeText(getApplicationContext(),"Item added to cart !!",Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(detailEquip.this);
                builder.setMessage("Do you want to add "+equp.getName()+" in cart?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("PName"));
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                equp=dataSnapshot.getValue(Equp.class);
                name.setText("Name : "+equp.getName());
                price.setText("Price : ₹ "+equp.getPrice());
                catagory.setText("Catagory : "+equp.getCatagory());
                seller.setText("Seller : "+equp.getSeller());
                detail.setText("                 "+dataSnapshot.child("description").getValue(String.class));
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
                dbUsers1 = FirebaseDatabase.getInstance().getReference("Posts");
                dbUsers1.orderByChild("catagory").equalTo(equp.getCatagory()).limitToFirst(5).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EqupList1.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Equp equp1 = snapshot.getValue(Equp.class);
                                assert equp1 != null;
                                if (!equp.getName().equals(equp1.getName()))
                                    if (!equp1.getSeller().equals(pref.getString("UName","")))
                                    EqupList1.add(equp1);
                            }
                            //recyclerView.scrollToPosition(0);
                            adapter1.notifyDataSetChanged();
                        }
                        else {
                            adapter1.notifyDataSetChanged();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("PName")).child("rating");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("avgrating").exists())
                {
                    rating=dataSnapshot.child("avgrating").getValue(Float.class);
                    noofpeople=dataSnapshot.child("people").getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference ref2= FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("PName")).child("rating").child("names");
        ref2.orderByChild("name").equalTo(pref.getString("UName","")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    ratingBar.setIsIndicator(true);
                    ratingBar.setRating(rating);
                    submitrating.setVisibility(View.INVISIBLE);
                   // submitrating.setText("Submitted");
                    //submitrating.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        submitrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref3=FirebaseDatabase.getInstance().getReference().child("Posts").child(getIntent().getStringExtra("PName")).child("rating");
                ref3.child("names").child(pref.getString("UName","")).child("name").setValue(pref.getString("UName",""));
                ref3.child("names").child(pref.getString("UName","")).child("rating").setValue(ratingBar.getRating());
                rating=((rating*noofpeople)+ratingBar.getRating())/(noofpeople+1);
                ref3.child("people").setValue(noofpeople+1);
                ref3.child("avgrating").setValue(rating);
                DatabaseReference ref4=FirebaseDatabase.getInstance().getReference().child("Users").child(equp.getSeller()).child("MyItems").child(getIntent().getStringExtra("PName")).child("rating");
                ref4.child("names").child(pref.getString("UName","")).child("name").setValue(pref.getString("UName",""));
                ref4.child("names").child(pref.getString("UName","")).child("rating").setValue(ratingBar.getRating());
                ref4.child("people").setValue(noofpeople+1);
                ref4.child("avgrating").setValue(rating);
                ratingBar.setIsIndicator(true);
                ratingBar.setRating(rating);
                submitrating.setVisibility(View.INVISIBLE);
               // submitrating.setText("Submitted");
                //submitrating.setEnabled(false);
            }
        });



    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
