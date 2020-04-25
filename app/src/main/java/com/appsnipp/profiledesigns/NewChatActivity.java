package com.appsnipp.profiledesigns;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsnipp.profiledesigns.AdapterFiles.MessageAdapter;
import com.appsnipp.profiledesigns.AdaptorClasses.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class NewChatActivity extends AppCompatActivity {

    private String mChatUser;
    TextView mUserName;
    TextView mUserLastSeen;
    CircleImageView mUserImage;
    private FirebaseAuth mAuth;

    String mCurrentUserId;

    DatabaseReference mDatabaseReference;
    private DatabaseReference mRootReference;

    private ImageButton mChatSendButton,mChatAddButton;
    private EditText mMessageView;

    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private final List<Message> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;

    SharedPreferences pref;
    public int a;
    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private int mCurrentPage = 1;

    //Solution for descending list on refresh
    private int itemPos = 0;
    private String mLastKey="";
    private String mPrevKey="";
    private Toolbar mToolBar;
    TextView mTitle;
//    private static final int GALLERY_PICK=1;
    StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pref = getSharedPreferences("user_details",MODE_PRIVATE);

        DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
        dbs.setValue("online");
        a=0;


        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (a==0)
                {
                    DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
                    dbs.setValue("online");
                    a=0;
                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);


        //mChatAddButton = (ImageButton)findViewById(R.id.chatAddButton);
        mChatSendButton = (ImageButton)findViewById(R.id.chatSendButton);
        mMessageView = (EditText)findViewById(R.id.chatMessageView);
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relchatlayout);
        //-----GETING FROM INTENT----
        mChatUser = getIntent().getStringExtra("ToName");
        String userName = getIntent().getStringExtra("user_name");

        //---SETTING ONLINE------
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbs1= FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser);
        dbs1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String stat=dataSnapshot.child("Status").getValue(String.class);
                if (stat.equals("online"))
                {
                    getSupportActionBar().setSubtitle("Online");
                    //holder.oloff.setImageResource(R.drawable.green);
                }
                else
                {
                    getSupportActionBar().setSubtitle("");
                    // holder.oloff.setImageResource(R.drawable.grey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        mMessagesList = findViewById(R.id.recycleViewMessageList);
        mMessageAdapter = new MessageAdapter(messagesList,getApplicationContext());
        mMessagesList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        mMessagesList.setAdapter(mMessageAdapter);

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                sendMessage(mMessageView.getText().toString());
                mMessageView.setText("");
            }
        });

        getUsername();
        loadMessages();

        InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mMessageView.getWindowToken(),0);
        mMessagesList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {

                mMessagesList.scrollToPosition(messagesList.size()-1);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMessage(String message){
        if(!TextUtils.isEmpty(message)){
            Map<String, Object> mes = new HashMap();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime now = LocalDateTime.now();
            mes.put("message",message);
            mes.put("from",pref.getString("UName",""));
            mes.put("to",mChatUser);
            mes.put("seen",false);
            mes.put("time",dtf.format(now));
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now1 = LocalDateTime.now();
            mDatabaseReference.child("Users").child(mChatUser).child("TimeStamp").setValue(dtf1.format(now1));
            mDatabaseReference.child("Users").child(pref.getString("UName","")).child("TimeStamp").setValue(dtf1.format(now1));
            mDatabaseReference.child("Messages").child(getChatRoomID(pref.getString("UName",""),mChatUser)).child("messages").push().setValue(mes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
        }
    }

    public void loadMessages(){
        mDatabaseReference.child("Messages").child(getChatRoomID(pref.getString("UName",""),mChatUser)).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        messagesList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                            Message message = postSnapshot.getValue(Message.class);
                            messagesList.add(message);
                        }
                        mMessagesList.scrollToPosition(mMessageAdapter.getItemCount()-1);
                        mMessageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    public static String getChatRoomID(String from,String to) {


        if (from.compareTo(to) > 0) {
            return from+to;
        } else {
            return to+from;
        }

    }

    public void getUsername(){
        mDatabaseReference.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                   // mTitle.setText(dataSnapshot.child("Name").getValue().toString());
                getSupportActionBar().setTitle(dataSnapshot.child("Name").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            DatabaseReference dbs= FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("UName","")).child("Status");
            dbs.setValue("offline");
            a=1;
        }
    }
    @Override
    public void onBackPressed() {
        a=1;
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
