package com.appsnipp.profiledesigns;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdapterFiles.Equadaptor;
import com.appsnipp.profiledesigns.AdapterFiles.TimeLineAdaptor;
import com.appsnipp.profiledesigns.AdapterFiles.horizontaladaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.AdaptorClasses.OrderAddress;
import com.appsnipp.profiledesigns.AdaptorClasses.OrderStatus;
import com.appsnipp.profiledesigns.AdaptorClasses.TimeLineModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class orderTracking extends AppCompatActivity {
    private TimeLineAdaptor adapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    DatabaseReference dbUsers;
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerView;
    String CType="";
    String path="";
    OrderAddress orderAddress=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences pref=getSharedPreferences("user_details",MODE_PRIVATE);
        setContentView(R.layout.activity_order_tracking);
        TextView itemname=(TextView)findViewById(R.id.itemname);
        Button dinfo=(Button)findViewById(R.id.dinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertnameedit = new AlertDialog.Builder(orderTracking.this);
                LayoutInflater factory = LayoutInflater.from(orderTracking.this);
                final View newview = factory.inflate(R.layout.orderinfo, null);
                final TextView address=(TextView)newview.findViewById(R.id.addressinfo);
                final TextView phone=(TextView)newview.findViewById(R.id.phoneinfo);
                final TextView postal=(TextView)newview.findViewById(R.id.postalcodeinfo);
                if (CType.equals("Customer"))
                {
                    path="orders";
                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child(path).child(getIntent().getStringExtra("ordid")).child("orderaddress");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            orderAddress=dataSnapshot.getValue(OrderAddress.class);
                            address.setText("Address : "+orderAddress.getAddress());
                            phone.setText("Customer Phone : "+orderAddress.getPhone());
                            postal.setText("Postal Code : "+orderAddress.getPostalCode());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    path="delivery";
                    DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child(path).child(getIntent().getStringExtra("ordid")).child("orderaddress");
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            orderAddress=dataSnapshot.getValue(OrderAddress.class);
                            address.setText("Address : "+orderAddress.getAddress());
                            phone.setText("Customer Phone : "+orderAddress.getPhone());
                            postal.setText("Postal Code : "+orderAddress.getPostalCode());
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                alertnameedit.setTitle("Order Address Information : ");
                alertnameedit.setView(newview);
                final AlertDialog dlg= alertnameedit.show();
            }
        });
        itemname.setText(getIntent().getStringExtra("PName"));
        CType=getIntent().getStringExtra("CType");
        setDataListItems();
        initRecyclerView();
    }

    private void initRecyclerView() {

        recyclerView =findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter= new TimeLineAdaptor(this, mDataList,CType,getIntent().getStringExtra("ordid"),getIntent().getStringExtra("customer"));
        recyclerView.setAdapter(adapter);

    }

    private void setDataListItems() {
        SharedPreferences pref=getSharedPreferences("user_details",MODE_PRIVATE);

        if (CType.equals("Customer"))
            path="orders";
        else
            path="delivery";
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child(path).child(getIntent().getStringExtra("ordid")).child("orderstatus");
        db.orderByChild("orderStatusno").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    mDataList.clear();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        TimeLineModel tlm=ds.getValue(TimeLineModel.class);
                        mDataList.add(tlm);
                    }
                    //Collections.reverse(mDataList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       // mDataList.add(new TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE));
      //  mDataList.add(new TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE));
      //  mDataList.add(new TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED));
       // mDataList.add(new TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED));
        //   mDataList.add(TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED));
        //mDataList.add(TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED));
        //mDataList.add(TimeLineModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED));
        // mDataList.add(TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED));
        // mDataList.add(TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED));

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
