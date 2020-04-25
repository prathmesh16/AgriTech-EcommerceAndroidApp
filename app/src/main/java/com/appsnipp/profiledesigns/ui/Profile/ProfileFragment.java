package com.appsnipp.profiledesigns.ui.Profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.CircleTransform;
import com.appsnipp.profiledesigns.EmailJavaFiles.SendMail;
import com.appsnipp.profiledesigns.Login_SignUpFiles.LogIn;
import com.appsnipp.profiledesigns.R;
import com.appsnipp.profiledesigns.AdaptorClasses.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private static final int GALLERY_INTENT=2;
    private ProgressDialog upload1;
    public StorageReference mstorage;
    User user;
    ImageView imageView;
    private TextView Email1;
public void onViewCreated(final View view, Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);
    final SharedPreferences prf;
    setHasOptionsMenu(true);
   // ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    prf = getActivity().getSharedPreferences("user_details",MODE_PRIVATE);
    /*Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    //setSupportActionBar(toolbar);
    toolbar.setLogo(R.drawable.ic_arrow_back_black_24dp);
    toolbar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(getActivity(), LogIn.class);
            SharedPreferences.Editor editor = prf.edit();
            editor.clear();
            editor.commit();
            startActivity(intent);
        }
    });
    Button posts=(Button)view.findViewById(R.id.button2);
    posts.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent =new Intent(getActivity(), EquFeed.class);
            startActivity(intent);
        }
    });*/
/*
    Button deleteu=(Button)view.findViewById(R.id.button3);
    deleteu.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Users").child(prf.getString("UName",""));
            myRef.removeValue();
            Intent intent=new Intent(getActivity(),LogIn.class);
            SharedPreferences.Editor editor = prf.edit();
            editor.clear();
            editor.commit();
            startActivity(intent);
        }
    });*/
    final TextView Name1=(TextView)view.findViewById(R.id.Name1);
    Email1 =(TextView)view.findViewById(R.id.Email1);
    final TextView  Name2=(TextView)view.findViewById(R.id.Name2);
    final TextView  Email2=(TextView)view.findViewById(R.id.Email2);
    final TextView  Phone2=(TextView)view.findViewById(R.id.Phone2);
    final TextView DOB2=(TextView)view.findViewById(R.id.DOB2);
    final TextView Addr2=(TextView)view.findViewById(R.id.Addr2);
    //ImageView editName=(ImageView)view.findViewById(R.id.editName);
    ImageView editEmail=(ImageView)view.findViewById(R.id.editEmail);
    ImageView editPhone=(ImageView)view.findViewById(R.id.editPhone);
    ImageView editDOB=(ImageView)view.findViewById(R.id.editDOB);
    ImageView editAddr=(ImageView)view.findViewById(R.id.editAddr);
    final ProgressBar progressBar=(ProgressBar)view.findViewById(R.id.progress);
    final ImageView profilepic=(ImageView)view.findViewById(R.id.profilePic);


// Read from the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Users").child(getActivity().getIntent().getStringExtra("UName"));
    myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
             user= dataSnapshot.getValue(User.class);
            assert user != null;
            Name1.setText(user.getName());
            Email1.setText(user.getEmail());
            Name2.setText(user.getName());
            Email2.setText((user.getEmail()));
            Phone2.setText(user.getPhone());
            Addr2.setText(user.getAddress());
            DOB2.setText(user.getDOB());

            // Log.d(TAG, "Value is: " + value);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            storageRef.child(user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(final Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.get().load(uri).transform(new CircleTransform()).into(profilepic);
                    progressBar.setVisibility(View.INVISIBLE);
                    profilepic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                            LayoutInflater factory = LayoutInflater.from(getActivity());
                            final View newview = factory.inflate(R.layout.sample, null);
                            imageView=newview.findViewById(R.id.dialog_imageview);
                            Button changeprofile=(Button)newview.findViewById(R.id.changeprofile);
                            Picasso.get().load(uri).into(imageView);
                            alertadd.setView(newview);
                            alertadd.show();
                            upload1=new ProgressDialog(getActivity());
                            changeprofile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent,GALLERY_INTENT);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

                /*
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://website-e1c2c.appspot.com").child(user.getImage());


                //Picasso.get()
                //        .load("https://console.firebase.google.com/project/website-e1c2c/storage/website-e1c2c.appspot.com/files/"+user.getImage())
                  //      .into(profilepic);

                try {
                    final File localFile = File.createTempFile("images", "jpg");
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            progressBar.setVisibility(View.INVISIBLE);
                            profilepic.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //System.out.println("Upload is " + progress + "% done");
                            int currentprogress = (int) progress;
                            progressBar.setProgress(currentprogress);
                        }
                    });
                } catch (IOException e ) {}*/

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            // Log.w(TAG, "Failed to read value.", error.toException());
        }
    });
/*
    editName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View newview = factory.inflate(R.layout.editalertdialouge, null);
            final EditText editable=newview.findViewById(R.id.editable);
            Button submit=newview.findViewById(R.id.submit);
            editable.setText(Name2.getText().toString());
            alertnameedit.setTitle("Name");
            alertnameedit.setView(newview);
            final AlertDialog dlg= alertnameedit.show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users/"+getActivity().getIntent().getStringExtra("UName"));
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("Name").setValue(editable.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Uploaded !", Toast.LENGTH_SHORT).show();
                    sendEmail("Name",Name2.getText().toString(),editable.getText().toString());
                    dlg.dismiss();
                }
            });


        }
    });
*/
    editEmail.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View newview = factory.inflate(R.layout.editalertdialouge, null);
            final EditText editable=newview.findViewById(R.id.editable);
            Button submit=newview.findViewById(R.id.submit);
            editable.setText(Email1.getText().toString());
            alertnameedit.setTitle("Email");
            alertnameedit.setView(newview);
            final AlertDialog dlg= alertnameedit.show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users/"+getActivity().getIntent().getStringExtra("UName"));
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("Email").setValue(editable.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Uploaded !", Toast.LENGTH_SHORT).show();
                    sendEmail("Email",Email1.getText().toString(),editable.getText().toString());
                    dlg.dismiss();
                }
            });

        }
    });

    editPhone.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View newview = factory.inflate(R.layout.editalertdialouge, null);
            final EditText editable=newview.findViewById(R.id.editable);
            Button submit=newview.findViewById(R.id.submit);
            editable.setText(Phone2.getText().toString());
            alertnameedit.setTitle("Phone");
            alertnameedit.setView(newview);
            final AlertDialog dlg= alertnameedit.show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users/"+getActivity().getIntent().getStringExtra("UName"));
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("Phone").setValue(editable.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Uploaded !", Toast.LENGTH_SHORT).show();
                    sendEmail("Phone",Phone2.getText().toString(),editable.getText().toString());
                    dlg.dismiss();
                }
            });


        }
    });

    editAddr.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View newview = factory.inflate(R.layout.editalertdialouge, null);
            final EditText editable=newview.findViewById(R.id.editable);
            Button submit=newview.findViewById(R.id.submit);
            editable.setText(Addr2.getText().toString());
            alertnameedit.setTitle("Address");
            alertnameedit.setView(newview);
            final AlertDialog dlg= alertnameedit.show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users/"+getActivity().getIntent().getStringExtra("UName"));
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("Address").setValue(editable.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Uploaded !", Toast.LENGTH_SHORT).show();
                    sendEmail("Address",Addr2.getText().toString(),editable.getText().toString());
                    dlg.dismiss();
                }
            });

        }
    });

    editDOB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View newview = factory.inflate(R.layout.editalertdialouge, null);
            final EditText editable=newview.findViewById(R.id.editable);
            editable.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            Button submit=newview.findViewById(R.id.submit);
            editable.setText(DOB2.getText().toString());
            alertnameedit.setTitle("DOB");
            alertnameedit.setView(newview);
            final AlertDialog dlg= alertnameedit.show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users/"+getActivity().getIntent().getStringExtra("UName"));
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myRef.child("DOB").setValue(editable.getText().toString());
                    Toast.makeText(getActivity(), "Successfully Uploaded !", Toast.LENGTH_SHORT).show();
                    sendEmail("Date Of Birth",DOB2.getText().toString(),editable.getText().toString());
                    dlg.dismiss();
                }
            });
            /*
            Intent editActivity=new Intent(getActivity(),editActivity.class);
            editActivity.putExtra("editable",DOB2.getText().toString());
            editActivity.putExtra("WhatToEdit","DOB");
            editActivity.putExtra("UName",getActivity().getIntent().getStringExtra("UName"));
            editActivity.putExtra("Email",Email2.getText().toString());
            startActivity(editActivity);

             */
        }
    });
    
    
}
    @Override

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    inflater.inflate(R.menu.menu_profile,menu);
        MenuItem DA =menu.add("Delete Account");
        DA.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                SharedPreferences prf;
                                prf = getActivity().getSharedPreferences("user_details",MODE_PRIVATE);
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Users").child(prf.getString("UName",""));
                                myRef.removeValue();
                                Intent intent=new Intent(getActivity(),LogIn.class);
                                SharedPreferences.Editor editor = prf.edit();
                                editor.clear();
                                editor.commit();
                                startActivity(intent);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want delete account?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            final byte[] fileInBytes = baos.toByteArray();


            FirebaseDatabase database1 = FirebaseDatabase.getInstance();
            DatabaseReference ref = database1.getReference("/Users/" + user.getName() + "/Image");
            mstorage = FirebaseStorage.getInstance().getReference().child(uri.getLastPathSegment());
            ref.setValue(uri.getLastPathSegment());
            //            StorageReference filepath=mstorage.child("Login_Data").child(uri.getLastPathSegment());
            mstorage.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    upload1.dismiss();
                    Toast.makeText(getActivity(), "Profile picture changed!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile,container,false);
    }
    protected void sendEmail(String whattoedit,String previous,String after) {
        //Creating SendMail object
        SendMail sm = new SendMail(getActivity(), Email1.getText().toString(),   whattoedit+" Changed !", "Your " + whattoedit + " has been succefully changed from "+previous+" to "+after);
        //Executing sendmail to send email
        sm.execute();

    }
}