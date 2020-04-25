package com.appsnipp.profiledesigns.ui.aboutus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AboutUsMap;
import com.appsnipp.profiledesigns.AdapterFiles.myorderadaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Order;
import com.appsnipp.profiledesigns.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class aboutusFragment extends Fragment {
public void onViewCreated(View view,Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);
    final TextView aboutus=(TextView)view.findViewById(R.id.messegeAboutUs);
    DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("AboutUs");
    db.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aboutus.setText(dataSnapshot.child("messege").getValue(String.class));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    ImageView imageView=(ImageView)view.findViewById(R.id.locationofficemap);
    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(getActivity(), AboutUsMap.class);
            startActivity(intent);
        }
    });

}
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_aboutus,container,false);

    }

}