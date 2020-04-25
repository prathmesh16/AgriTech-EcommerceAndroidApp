package com.appsnipp.profiledesigns.ui.tools;


import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toolbar;

import com.appsnipp.profiledesigns.AdapterFiles.Equadaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ToolsFragment extends Fragment {
    private Equadaptor adapter;
    private List<Equp> EqupList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;
    SharedPreferences pref;
    boolean isLoading = false;
    String orderbystr="name";
    int eleCont=50;
    int elecntprev=50;
    int flag =0;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    public void onViewCreated(View view,Bundle savedInstanceState)
    {
        super.onViewCreated(view,savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        pref= getActivity().getSharedPreferences("user_details",MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        EqupList = new ArrayList<>();
        adapter = new Equadaptor(getContext(), EqupList);
        recyclerView.setAdapter(adapter);
        // recyclerView.setHasFixedSize(true);
        dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
        dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EqupList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Equp equp = snapshot.getValue(Equp.class);
                        assert equp != null;
                        if (!equp.getSeller().equals(pref.getString("UName","")))
                            if(equp.getCatagory().equals("Farming Tool"))
                            EqupList.add(equp);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        initScrollListener();
        setHasOptionsMenu(true);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
        }


    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_equ_feed,container,false);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                flag=0;
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (EqupList.size()==eleCont)
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == EqupList.size() - 1&&flag==0) {
                        //bottom of list!
                        elecntprev=eleCont;
                        eleCont+=5;
                        dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                        dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                EqupList.clear();
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Equp equp = snapshot.getValue(Equp.class);
                                        assert equp != null;
                                        if (!equp.getSeller().equals(pref.getString("UName","")))
                                            if(equp.getCatagory().equals("Farming Tool"))
                                            EqupList.add(equp);
                                    }
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(),"Bottom",Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });



                    }
            }
        });


    }


    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        MenuItem setting= menu.findItem(R.id.action_settings);
        MenuItem OBP =menu.add("Order By Price");
        MenuItem  OBN =menu.add("Order By Name");
        MenuItem  OBC =menu.add("Order By Catagory");
        OBP.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                orderbystr="price";
                dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EqupList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Equp equp = snapshot.getValue(Equp.class);
                                assert equp != null;
                                if (!equp.getSeller().equals(pref.getString("UName","")))
                                    if(equp.getCatagory().equals("Farming Tool"))
                                    EqupList.add(equp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                return false;
            }
        });

        OBN.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                orderbystr="name";
                dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EqupList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Equp equp = snapshot.getValue(Equp.class);
                                assert equp != null;
                                if (!equp.getSeller().equals(pref.getString("UName","")))
                                    if(equp.getCatagory().equals("Farming Tool"))
                                    EqupList.add(equp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                return false;
            }
        });


        OBC.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                orderbystr="catagory";
                dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EqupList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Equp equp = snapshot.getValue(Equp.class);
                                assert equp != null;
                                if (!equp.getSeller().equals(pref.getString("UName","")))
                                    if(equp.getCatagory().equals("Farming Tool"))
                                    EqupList.add(equp);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    flag=1;
                    dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                    dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).startAt(newText).endAt(newText+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            EqupList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Equp equp = snapshot.getValue(Equp.class);
                                    assert equp != null;
                                    if (!equp.getSeller().equals(pref.getString("UName","")))
                                        if(equp.getCatagory().equals("Farming Tool"))
                                        EqupList.add(equp);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    // Toast.makeText(EquFeed.this,"searching",Toast.LENGTH_LONG).show();
                    return false;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    flag=1;
                    dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                    dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            EqupList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Equp equp = snapshot.getValue(Equp.class);
                                    assert equp != null;
                                    if (!equp.getSeller().equals(pref.getString("UName","")))
                                        if(equp.getCatagory().equals("Farming Tool"))
                                        EqupList.add(equp);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //Toast.makeText(EquFeed.this,"submitted",Toast.LENGTH_LONG).show();
                    return false;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Intent i = new Intent(getActivity(), cart.class);
                i.putExtra("UName",getActivity().getIntent().getStringExtra("UName"));
                this.startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}