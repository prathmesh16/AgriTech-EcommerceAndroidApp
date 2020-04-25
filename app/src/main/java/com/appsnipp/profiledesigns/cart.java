package com.appsnipp.profiledesigns;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.appsnipp.profiledesigns.AdapterFiles.cartadaptor;
import com.appsnipp.profiledesigns.AdapterFiles.cartadaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;

public class cart extends AppCompatActivity {
    private cartadaptor adapter;
    private List<Equp> EqupList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;
    SharedPreferences pref;
    boolean isLoading = false;
    String orderbystr="name";
    int eleCont=5;
    int flag =0;
    ImageView bck;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recyclerviewcart);
        // recyclerView.setHasFixedSize(true);
        bck=(ImageView)findViewById(R.id.imageView7);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        EqupList = new ArrayList<>();
        adapter = new cartadaptor(this, EqupList);
        recyclerView.setAdapter(adapter);
        dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(pref.getString("UName","")).child("cart");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EqupList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Equp equp = snapshot.getValue(Equp.class);
                        assert equp != null;
                        EqupList.add(equp);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    if (EqupList.size()==0)
                    {
                        adapter.notifyDataSetChanged();
                        bck.setImageResource(R.drawable.emptycart);
                    }
                    else {
                        bck.setImageResource(R.drawable.back);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
