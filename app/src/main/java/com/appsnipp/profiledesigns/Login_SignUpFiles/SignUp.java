package com.appsnipp.profiledesigns.Login_SignUpFiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.EmailJavaFiles.SendMail;
import com.appsnipp.profiledesigns.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class SignUp extends AppCompatActivity {
    private static final int GALLERY_INTENT=2;
    public StorageReference mstorage;
    private ProgressDialog upload1;
    private EditText name;
    private EditText pass;
    private EditText confpass;
    private EditText phone;
    private EditText email;
    private  Button SignUp;
    ImageView upImage;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        upImage=(ImageView) findViewById(R.id.profilePic);
        name=(EditText)findViewById(R.id.usr);
         pass=(EditText)findViewById(R.id.pass);
         confpass=(EditText)findViewById(R.id.confpass);
         email=(EditText)findViewById(R.id.email);
         phone=(EditText)findViewById(R.id.phone);
         SignUp=(Button)findViewById(R.id.signup_btn);
        getSupportActionBar().hide();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().equals("")||email.getText().toString().equals("")||phone.getText().toString().equals("")||pass.getText().toString().equals("")) {
                    Toast.makeText(SignUp.this,"All fields are Compulsary!",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(SignUp.this, "Please select profile picture!", Toast.LENGTH_SHORT).show();
            }
        });
        upload1=new ProgressDialog(this);
        upload1.setIndeterminate(false);
        upload1.setCanceledOnTouchOutside(false);
        upImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });


    }
    public String genCustUniqueId() {
        Random r = new Random(System.currentTimeMillis());
        return "Cust" + (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            upImage.setImageURI(uri);

            SignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(name.getText().toString().equals("")||email.getText().toString().equals("")||phone.getText().toString().equals("")||pass.getText().toString().equals("")) {
                        Toast.makeText(SignUp.this,"All fields are Compulsary!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!pass.getText().toString().equals(confpass.getText().toString()))
                    {
                        Toast.makeText(SignUp.this,"Password doesen't match!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(phone.getText().toString().length()!=10)
                    {
                        Toast.makeText(SignUp.this,"Phone No should be 10 digit!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(!email.getText().toString().endsWith("@gmail.com"))
                    {
                        Toast.makeText(SignUp.this,"Please Enter valid Email Id!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        upload1.setMessage("Please Wait");
                        upload1.show();
                    }
                    final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference();
                    ref1.child("Users").orderByChild("Name").equalTo(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                upload1.setMessage("Signing UP !!");
                                upload1.show();
                                DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                LocalDateTime now1 = LocalDateTime.now();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Users");
                                myRef.child(name.getText().toString()).child("Name").setValue(name.getText().toString());
                                myRef.child(name.getText().toString()).child("Password").setValue(pass.getText().toString());
                                myRef.child(name.getText().toString()).child("Email").setValue(email.getText().toString());
                                myRef.child(name.getText().toString()).child("Phone").setValue(phone.getText().toString());
                                myRef.child(name.getText().toString()).child("CustId").setValue(genCustUniqueId());
                                myRef.child(name.getText().toString()).child("TimeStamp").setValue(dtf1.format(now1));
                                myRef.child(name.getText().toString()).child("Status").setValue("offline");
                                SendMail sm = new SendMail(SignUp.this, email.getText().toString(), "Account Created!", "You are succcessfully registered in PestiCom!!");
                                //Executing sendmail to send email
                                sm.execute();
                                Uri uri = data.getData();

                                Bitmap bmp = null;
                                try {
                                    bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                //here you can choose quality factor in third parameter(ex. i choosen 25)
                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                final byte[] fileInBytes = baos.toByteArray();


                                FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                DatabaseReference ref = database1.getReference("/Users/" + name.getText().toString() + "/Image");
                                mstorage = FirebaseStorage.getInstance().getReference().child(uri.getLastPathSegment());
                                ref.setValue(uri.getLastPathSegment());
                                // StorageReference filepath=mstorage.child("Login_Data").child(uri.getLastPathSegment());
                                mstorage.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        upload1.dismiss();
                                        Toast.makeText(SignUp.this, "Done!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                        intent.putExtra("UName", getIntent().getStringExtra("UName"));
                                        startActivity(intent);

                                    }
                                });
                            }
                            else{
                                    upload1.dismiss();
                                    Toast.makeText(getApplicationContext(), "Username Already exist !! Please select another Username", Toast.LENGTH_LONG).show();
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(SignUp.this, "Settings!", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
