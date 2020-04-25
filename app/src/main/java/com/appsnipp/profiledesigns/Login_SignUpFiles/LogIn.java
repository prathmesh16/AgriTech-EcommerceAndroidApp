package com.appsnipp.profiledesigns.Login_SignUpFiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.MainActivity;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.forgotPassword;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {
    SharedPreferences pref,lgot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getSupportActionBar().hide();
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        if(pref.contains("UName") && pref.contains("UPass")){
            intent.putExtra("UName",pref.getString("UName",""));
            intent.putExtra("UPass", pref.getString("UPass",""));
            startActivity(intent);
        }
        final EditText UName1=(EditText)findViewById(R.id.usr);
        final EditText UPass1=(EditText)findViewById(R.id.pass);
       /*Button noti=(Button)findViewById(R.id.button7);
        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNotification();
            }
        });*/
        final TextView forgpass=(TextView)findViewById(R.id.forgotpass);
        forgpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(LogIn.this, forgotPassword.class);
                startActivity(i);
            }
        });
       Button signup=(Button)findViewById(R.id.signup_btn);
       signup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(),SignUp.class);
               startActivity(intent);
           }
       });
        final ProgressDialog ulogin=new ProgressDialog(this);
        Button button=(Button)findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ulogin.setMessage("Loading !!");
                ulogin.show();
                //  FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Users/"+UName1.getText().toString().trim());
                //  DatabaseReference myref = database.getReference("/User/"+UName1.getText().toString().trim()+"/Name");
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists())
                        {
                            ulogin.dismiss();
                            Toast.makeText(LogIn.this,"Account Does Not Exist!!",Toast.LENGTH_SHORT).show();
                        }
                            if (TextUtils.isEmpty(UName1.getText().toString().trim()))
                            {
                                ulogin.dismiss();
                                Toast.makeText(LogIn.this,"Please enter Name!!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (TextUtils.isEmpty(UPass1.getText().toString().trim()))
                            {
                                ulogin.dismiss();
                                Toast.makeText(LogIn.this,"Please enter Password!!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(LogIn.this, "Logging In !!", Toast.LENGTH_SHORT).show();
                            if (UName1.getText().toString().trim().equals(dataSnapshot.child("Name").getValue(String.class))){
                                if(UPass1.getText().toString().trim().equals(dataSnapshot.child("Password").getValue(String.class))) {
                                    ulogin.dismiss();
                                    Toast.makeText(LogIn.this, "Logged In Succesfully..!!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("UName", UName1.getText().toString().trim());
                                    intent.putExtra("UPass", UPass1.getText().toString().trim());
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("UName",UName1.getText().toString().trim());
                                    editor.putString("UPass",UPass1.getText().toString().trim());
                                    editor.commit();
                                    lgot = getSharedPreferences("user_lgot_details",MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = lgot.edit();
                                    editor1.putString("UName",UName1.getText().toString().trim());
                                    editor1.commit();
                                    startActivity(intent);
                                }
                                else {
                                    ulogin.dismiss();
                                    Toast.makeText(LogIn.this,"Password is Incorrect!!",Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                //do something if not exists
                                ulogin.dismiss();
                                Toast.makeText(LogIn.this,"Account Does Not Exist!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
           /* @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                 String value = dataSnapshot.getValue(String.class);
                        TextView t=(TextView)findViewById(R.id.textView2);
                        t.setText(""+value);
                        if(value.equals(UName1.getText().toString().trim()))
                        {
                            Toast.makeText(Log_In.this,"Logged In Succesfully..!!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(Log_In.this,"Log In Failed..!!",Toast.LENGTH_SHORT).show();
                        }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }*/
                });
            }
        });


    }
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}
