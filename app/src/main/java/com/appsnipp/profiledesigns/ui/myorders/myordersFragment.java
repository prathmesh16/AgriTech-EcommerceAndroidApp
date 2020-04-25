package com.appsnipp.profiledesigns.ui.myorders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appsnipp.profiledesigns.AdapterFiles.myorderadaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.AdaptorClasses.Order;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.ui.addItem.addItemFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myordersFragment extends Fragment {
    private myorderadaptor adapter;
    private List<Order> EqupList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;
    SharedPreferences pref;
    boolean isLoading = false;
    String orderbystr = "name";
    int eleCont = 5;
    int flag = 0;
    private SearchView searchView = null;
    String CType = "customer";
     ImageView bck;
    private SearchView.OnQueryTextListener queryTextListener;

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclervieworder);
        // recyclerView.setHasFixedSize(true);

        bck=(ImageView)view.findViewById(R.id.noorder);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EqupList = new ArrayList<>();
        adapter = new myorderadaptor(getContext(), EqupList, CType);
        recyclerView.setAdapter(adapter);
        dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(getActivity().getIntent().getStringExtra("UName")).child("orders");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EqupList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order equp = snapshot.getValue(Order.class);
                        assert equp != null;
                        EqupList.add(equp);
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
                    if (EqupList.size()==0)
                    {
                        adapter.notifyDataSetChanged();
                        bck.setImageResource(R.drawable.noorders);
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
        setHasOptionsMenu(true);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_orders, container, false);
    }
}