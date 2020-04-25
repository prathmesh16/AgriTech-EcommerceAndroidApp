package com.appsnipp.profiledesigns.ui.home;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.appsnipp.profiledesigns.AdapterFiles.Equadaptor;
import com.appsnipp.profiledesigns.AdapterFiles.SlidingImage_Adapter;
import com.appsnipp.profiledesigns.AdapterFiles.horizontaladaptor;
import com.appsnipp.profiledesigns.AdapterFiles.horizontaladaptor;
import com.appsnipp.profiledesigns.AdaptorClasses.Equp;
import com.appsnipp.profiledesigns.AdaptorClasses.ImageModel;
import com.appsnipp.profiledesigns.MainActivity;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.cart;
import com.appsnipp.profiledesigns.ui.addItem.addItemFragment;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;
import static android.widget.Toast.*;

public class homeFragment extends Fragment {
    private SlidingImage_Adapter slidingImage_adapter;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;

    private String[] myImageList = new String[]{"https://www.kiwimana.co.nz/wp-content/uploads//2012/07/101_sdc149111.jpg","https://www.pan-uk.org/site/wp-content/uploads/Farmer-spraying-pesticides-on-soy-crop-in-India-Shutterstock-Small.jpg","https://civileats.com/wp-content/uploads/2019/04/190410-civil-eats-roundtable-gmos-pesticides-trends-top-1-1200x800.jpg","https://edu.glogster.com/proxy?url=http%3A%2F%2Fi.huffpost.com%2Fgen%2F1586408%2Fimages%2Fo-DDT-facebook.jpg","https://th.thgim.com/news/cities/Kochi/article20462224.ece/alternates/FREE_660/15KI-PESTICIDE"};
    //private String[] myImageList = new String[]{"https://cdn.pixabay.com/photo/2013/07/18/10/56/railroad-tracks-163518_1280.jpg","https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885_1280.jpg","https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832_1280.jpg","http://www.sandybeachinternational.com/wp-content/uploads/2018/11/cropped-beach-exotic-holiday-248797.jpg","https://cdn.pixabay.com/photo/2016/11/14/04/45/elephant-1822636_1280.jpg"};
    private String[] images=new String[5];
    private LinearLayout linerlayouttop;
    private AppBarLayout appBarLayout;

    private horizontaladaptor adapter1;
    private List<Equp> EqupList1;
    DatabaseReference dbUsers1;
    RecyclerView recyclerView1;

    SharedPreferences pref;

    private Equadaptor adapter;
    private List<Equp> EqupList;
    DatabaseReference dbUsers;
    RecyclerView recyclerView;

    SearchView searchView = null;
    SearchView.OnQueryTextListener queryTextListener;
    ShimmerFrameLayout progress2;
    boolean isLoading = false;
    String orderbystr="name";
    int eleCont=10;
    int elecntprev=10;
    int flag =0;
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public void onViewCreated(View view, Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);
    pref= Objects.requireNonNull(getActivity()).getSharedPreferences("user_details",MODE_PRIVATE);
    // recyclerView.setHasFixedSize(true);
    linerlayouttop=view.findViewById(R.id.layouthometop);
    appBarLayout=view.findViewById(R.id.appbar);
    progress2=(ShimmerFrameLayout)view.findViewById(R.id.progrees2);
    DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
    dbs.setValue("offline");

    imageModelArrayList = new ArrayList<>();

    DatabaseReference dbi=FirebaseDatabase.getInstance().getReference().child("HomeImages");
    dbi.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            imageModelArrayList.clear();
            currentPage=0;
            NUM_PAGES=0;
            for (DataSnapshot ds:dataSnapshot.getChildren())
            {
                ImageModel imageModel=ds.getValue(ImageModel.class);
                imageModelArrayList.add(imageModel);
            }
            init();
            slidingImage_adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    //imageModelArrayList = populateList();

    recyclerView1 = view.findViewById(R.id.horizontalrecyclerview);
    setHasOptionsMenu(true);
    recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    EqupList1 = new ArrayList<>();
    adapter1 = new horizontaladaptor(getContext(), EqupList1);
    recyclerView1.setAdapter(adapter1);
    dbUsers1 = FirebaseDatabase.getInstance().getReference("Posts");
    dbUsers1.orderByChild("rating/avgrating").limitToLast(10).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
            EqupList1.clear();
            progress2.setVisibility(View.INVISIBLE);
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Equp equp = snapshot.getValue(Equp.class);
                    assert equp != null;
                    if (!equp.getSeller().equals(pref.getString("UName","")))
                         EqupList1.add(equp);
                }
                Collections.reverse(EqupList1);
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
    initScrollListener();
    setHasOptionsMenu(true);
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
    }

}
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home,container,false);
    }
    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


       // inflater.inflate(R.menu.home_menu, menu);
       // MenuItem cartItem = menu.findItem(R.id.action_carthome);
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem OBP =menu.add("Order By Price");
        MenuItem  OBN =menu.add("Order By Name");
        MenuItem  OBC =menu.add("Order By Catagory");
        OBP.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                orderbystr="price";
                appBarLayout.setExpanded(false);
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
                appBarLayout.setExpanded(false);
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
                appBarLayout.setExpanded(false);
                dbUsers = FirebaseDatabase.getInstance().getReference("Posts");
                dbUsers.orderByChild(orderbystr).limitToFirst(eleCont).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        EqupList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Equp equp = snapshot.getValue(Equp.class);
                                assert equp != null;
                                if (!equp.getSeller().equals(pref.getString("UName","")))
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
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public boolean onQueryTextChange(String newText) {
                    flag=1;

                    appBarLayout.setExpanded(false);
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
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    appBarLayout.setVisibility(View.VISIBLE);
                    linerlayouttop.setVisibility(View.VISIBLE);
                    return false;
                }
            });
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
                                        EqupList.add(equp);
                                    }
                                    adapter.notifyDataSetChanged();
                                    //Toast.makeText(getActivity(),"Bottom",Toast.LENGTH_LONG).show();
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

    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImageUri(myImageList[i]);
            //imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }

    private void init() {

        mPager = (ViewPager) getActivity().findViewById(R.id.pager);
        slidingImage_adapter=new SlidingImage_Adapter(getActivity(),imageModelArrayList);
        mPager.setAdapter(slidingImage_adapter);

        CirclePageIndicator indicator = (CirclePageIndicator)
                getActivity().findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

    }


}