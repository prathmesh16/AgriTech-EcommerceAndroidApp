package com.appsnipp.profiledesigns;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdaptorClasses.User;
import com.appsnipp.profiledesigns.Login_SignUpFiles.LogIn;
import com.appsnipp.profiledesigns.ui.Delivery.deliveryFragment;
import com.appsnipp.profiledesigns.ui.Profile.ProfileFragment;
import com.appsnipp.profiledesigns.ui.aboutus.aboutusFragment;
import com.appsnipp.profiledesigns.ui.addItem.addItemFragment;
import com.appsnipp.profiledesigns.ui.chats.chatsFragment;
import com.appsnipp.profiledesigns.ui.feedback.feedbackFragment;
import com.appsnipp.profiledesigns.ui.home.homeFragment;
import com.appsnipp.profiledesigns.ui.myItem.myItemFragment;
import com.appsnipp.profiledesigns.ui.myorders.myordersFragment;
import com.appsnipp.profiledesigns.ui.tools.ToolsFragment;
import com.appsnipp.profiledesigns.ui.viewPesticides.viewPesticidesFragment;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         pref= getSharedPreferences("user_details",MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 23);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.getMenu().getItem(0).setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
        navigationView.setNavigationItemSelectedListener(this);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(pref.getString("UName",""));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                View headerView = navigationView.getHeaderView(0);
                TextView navHeaderEmail=(TextView)headerView.findViewById(R.id.textViewNavEmail);
                navHeaderEmail.setText(""+user.getEmail());


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                storageRef.child(user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).transform(new CircleTransform()).into((ImageView) navigationView.findViewById(R.id.profilePic));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                // Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
boolean ishome=true;
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(ishome==false)
            {
                navigationView.getMenu().getItem(0).setChecked(true);
                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_home));
            }
            else
            {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                moveTaskToBack(true);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do you want exit?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this,"Settings !",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int viewId = item.getItemId();
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = new homeFragment();
                title  = "Home";
                ishome=true;
                break;
            case R.id.nav_Profile:
                fragment = new ProfileFragment();
                title  = "Profile";
                ishome=false;
                break;
            case R.id.nav_addItem:
                fragment = new addItemFragment();
                title  = "Add Item";
                ishome=false;
                break;
            case R.id.nav_Pesticides:
                fragment = new viewPesticidesFragment();
                title  = "Pesticides";
                ishome=false;
                break;
            case R.id.nav_myItem:
                fragment = new myItemFragment();
                title  = "My Items";
                ishome=false;
                break;
            case R.id.nav_orders:
                fragment = new myordersFragment();
                title  = "My Orders";
                ishome=false;
                break;
            case R.id.nav_aboutus:
                fragment = new aboutusFragment();
                title  = "About Us";
                ishome=false;
                break;
            case R.id.nav_feedback:
                fragment = new feedbackFragment();
                title  = "Feedback";
                ishome=false;
                break;
            case R.id.nav_delivery:
                fragment = new deliveryFragment();
                title  = "Delivery";
                ishome=false;
                break;
            case R.id.nav_chat:
                fragment = new chatsFragment();
                title  = "Chats";
                ishome=false;
                break;
            case R.id.nav_tools:
                fragment = new ToolsFragment();
                title  = "Tools";
                ishome=false;
                break;
            case R.id.nav_logout:
                final SharedPreferences prf;
                prf =getSharedPreferences("user_details",MODE_PRIVATE);
                Intent intent =new Intent(getApplicationContext(), LogIn.class);
                SharedPreferences.Editor editor = prf.edit();
                editor.clear();
                editor.commit();
                startActivity(intent);
                ishome=false;
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
