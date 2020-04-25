package com.appsnipp.profiledesigns;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appsnipp.profiledesigns.EmailJavaFiles.SendMail;
import com.appsnipp.profiledesigns.Login_SignUpFiles.LogIn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class forgotPassword extends AppCompatActivity {
public String email;
public int OTP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        final EditText uname=(EditText)findViewById(R.id.usernamefpass);
        final Button sendotp=(Button)findViewById(R.id.button2);
        final ProgressBar progressBar=(ProgressBar)findViewById(R.id.progressBar);
        final TextInputLayout tl=(TextInputLayout)findViewById(R.id.unameforgpass);
        progressBar.setVisibility(View.INVISIBLE);
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
                db.orderByChild("Name").equalTo(uname.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tl.setVisibility(View.INVISIBLE);
                            uname.setVisibility(View.INVISIBLE);
                            sendotp.setVisibility(View.INVISIBLE);
                            email = dataSnapshot.child(uname.getText().toString()).child("Email").getValue(String.class);
                            OTP=genOTP();
                            SendMail sm = new SendMail(forgotPassword.this, email, "OTP for resetting Password", "Your OTP is : " + OTP);
                            //Executing sendmail to send email
                            sm.execute();
                            Toast.makeText(forgotPassword.this,"OTP is sent! Plaese check your registered Email!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else
                        {
                            uname.setText("");
                            Toast.makeText(forgotPassword.this,"Username does not exist!",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        final EditText otpedit=(EditText)findViewById(R.id.otp);
        final EditText pass=(EditText)findViewById(R.id.pass);
        final EditText cpass=(EditText)findViewById(R.id.confpass);

        Button changepass=(Button)findViewById(R.id.button5);
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otpedit.getText().toString().equals(String.valueOf(OTP)))
                {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(uname.getText().toString());
                    db.child("Password").setValue(pass.getText().toString());
                    SendMail sm = new SendMail(forgotPassword.this, email,"Password Changed!" ,"Your Password has been successfully changed!");
                    //Executing sendmail to send email
                    sm.execute();
                    Toast.makeText(forgotPassword.this,"Password successfully changed!",Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(forgotPassword.this, LogIn.class);
                    startActivity(i);
                }
                else if (!otpedit.getText().toString().equals(String.valueOf(OTP)))
                {
                    Toast.makeText(forgotPassword.this,"OTP is wrong!",Toast.LENGTH_SHORT).show();
                }
                else if ( pass.getText().toString().equals(cpass.getText().toString()))
                {
                    Toast.makeText(forgotPassword.this,"Password does not match!",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public int genOTP() {
        Random r = new Random(System.currentTimeMillis());
        return (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }
}
