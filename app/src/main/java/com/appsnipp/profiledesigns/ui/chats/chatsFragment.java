package com.appsnipp.profiledesigns.ui.chats;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.appsnipp.profiledesigns.AdapterFiles.useradaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.AdaptorClasses.Order;
import com.appsnipp.profiledesigns.AdaptorClasses.User;
import com.appsnipp.profiledesigns.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class chatsFragment extends Fragment {
    private useradaptor adapter;
    private List<User> UserList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;
    SharedPreferences pref,lgot;
    boolean isLoading = false;
    String orderbystr="name";
    int eleCont=5;
    int flag =0;
    private SearchView searchView = null;
    private String CType="Seller";
    public int a=0;
    private SearchView.OnQueryTextListener queryTextListener;
public void onViewCreated(View view,Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);
    pref = getActivity().getSharedPreferences("user_details",MODE_PRIVATE);

    lgot = getActivity().getSharedPreferences("user_lgot_details",MODE_PRIVATE);

    DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
    dbs.setValue("online");
    a=0;
    recyclerView = view.findViewById(R.id.userrecyclerview);
    // recyclerView.setHasFixedSize(true);


    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    UserList = new ArrayList<>();
    adapter = new useradaptor(getContext(), UserList);
    recyclerView.setAdapter(adapter);
    dbUsers = FirebaseDatabase.getInstance().getReference("Users");
    dbUsers.orderByChild("TimeStamp").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UserList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if (!user.getName().equals(pref.getString("UName","")))
                            UserList.add(user);
                }
                Collections.reverse(UserList);
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
        return inflater.inflate(R.layout.activity_users,container,false);
    }
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem cartItem = menu.findItem(R.id.action_cart);
                cartItem.setVisible(false);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            queryTextListener = new SearchView.OnQueryTextListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onQueryTextChange(String newText) {
                    dbUsers = FirebaseDatabase.getInstance().getReference("Users");
                    dbUsers.orderByChild("Name").startAt(newText).endAt(newText+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    assert user != null;
                                    if (!user.getName().equals(pref.getString("UName","")))
                                        UserList.add(user);
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

                    dbUsers = FirebaseDatabase.getInstance().getReference("Users");
                    dbUsers.orderByChild("Name").startAt(query).endAt(query+"\uf8ff").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    User user = snapshot.getValue(User.class);
                                    assert user != null;
                                    if (!user.getName().equals(pref.getString("UName","")))
                                        UserList.add(user);
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
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return false;
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        a=0;
        DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
        dbs.setValue("online");
    }
    @Override
    public void onStop() {
        super.onStop();
        if(a==0) {
            DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(lgot.getString("UName","")).child("Status");
            dbs.setValue("offline");
        }
    }

}