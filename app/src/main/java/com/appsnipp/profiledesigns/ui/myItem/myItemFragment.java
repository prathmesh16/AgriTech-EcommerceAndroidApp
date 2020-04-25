package com.appsnipp.profiledesigns.ui.myItem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdapterFiles.myItemadaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.cart;
import com.appsnipp.profiledesigns.ui.addItem.addItemFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class myItemFragment extends Fragment {
    private myItemadaptor adapter;
    private List<Equp> EqupList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;
    SharedPreferences pref;
    boolean isLoading = false;
    String orderbystr="name";
    int eleCont=5;
    int flag =0;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
public void onViewCreated(View view,Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);

    recyclerView = view.findViewById(R.id.recyclerView);
    // recyclerView.setHasFixedSize(true);


    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    EqupList = new ArrayList<>();
    adapter = new myItemadaptor(getContext(), EqupList);
    recyclerView.setAdapter(adapter);
    dbUsers = FirebaseDatabase.getInstance().getReference("Users").child(getActivity().getIntent().getStringExtra("UName")).child("MyItems");
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
                adapter.notifyDataSetChanged();
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
        return inflater.inflate(R.layout.activity_myitem_feed,container,false);
    }
    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.myitem, menu);
        MenuItem add =menu.findItem(R.id.action_addItem);
        add.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                 addItemFragment fragment2 = new addItemFragment();
                FragmentManager fragmentManager = getFragmentManager();
                final NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(2).setChecked(true);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment2);
                fragmentTransaction.commit();
                return false;
            }
        });
        MenuItem setting= menu.findItem(R.id.action_settings);

        super.onCreateOptionsMenu(menu, inflater);

    }




}