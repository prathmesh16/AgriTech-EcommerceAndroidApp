package com.appsnipp.profiledesigns.ui.addItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class addItemFragment extends Fragment {
    private static final int GALLERY_INTENT=2;
    public StorageReference mstorage;
    private ProgressDialog upload;
    private Button addItem;
    private Spinner spinner;
    private EditText name;
    private EditText price;
    private EditText desc;
    private EditText qty;
    private ImageView image;
    private SharedPreferences prf;
public void onViewCreated(View view,Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);

    prf = getActivity().getSharedPreferences("user_details",MODE_PRIVATE);
    addItem = (Button)view.findViewById(R.id.button4);
    spinner = (Spinner)view.findViewById(R.id.catagory);
     name=(EditText)view.findViewById(R.id.name);
     price=(EditText)view.findViewById(R.id.price);
     desc=(EditText)view.findViewById(R.id.description);
     qty = (EditText)view.findViewById(R.id.qty);
     image=(ImageView)view.findViewById(R.id.itemPhoto);

    upload=new ProgressDialog(getActivity());
   image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,GALLERY_INTENT);
        }
    });

    addItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "Please select picture!", Toast.LENGTH_SHORT).show();
        }
    });


}

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_INTENT&&resultCode==RESULT_OK)
        {

            Uri uri=data.getData();
            image.setImageURI(uri);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            //here you can choose quality factor in third parameter(ex. i choosen 25)
            assert bmp != null;
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            final byte[] fileInBytes = baos.toByteArray();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("Users");
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (name.getText().toString().equals("")||qty.getText().toString().equals("")||price.getText().toString().equals("")||desc.getText().toString().equals(""))
                    {
                        Toast.makeText(getActivity(),"Please fill all data!",Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Toast.makeText(addItem.this, String.valueOf(spinner.getSelectedItem()),Toast.LENGTH_LONG).show();
                    final DatabaseReference ref1= FirebaseDatabase.getInstance().getReference();
                    ref1.child("Posts").orderByChild("name").equalTo(name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists())
                            {
                                upload.setMessage("Uploading !!");
                                upload.show();
                                DatabaseReference db=ref1.child("Posts").child(name.getText().toString());
                                Map<String, Object> obj = new HashMap();
                                obj.put("price",Integer.parseInt(price.getText().toString()));
                                obj.put("name",name.getText().toString());
                                obj.put("quantity",qty.getText().toString());
                                obj.put("description",desc.getText().toString());
                                obj.put("catagory",spinner.getSelectedItem());
                                obj.put("seller",prf.getString("UName",""));

                                Map<String, Object> rat = new HashMap();
                                rat.put("avgrating",0);
                                rat.put("people",0);
                                obj.put("rating",rat);
                                /*
                                db.child("price").setValue(Integer.parseInt(price.getText().toString()));
                                db.child("name").setValue(name.getText().toString());
                                db.child("quantity").setValue(qty.getText().toString());
                                db.child("description").setValue(desc.getText().toString());
                                db.child("catagory").setValue(spinner.getSelectedItem());
                                db.child("seller").setValue(prf.getString("UName",""));
                                db.child("rating").child("avgrating").setValue(0);
                                db.child("rating").child("people").setValue(0);
                                */
                                db.setValue(obj);

                                DatabaseReference dbn=ref1.child("Users").child(prf.getString("UName","")).child("MyItems").child(name.getText().toString());
                               /*
                                dbn.child("price").setValue(Integer.parseInt(price.getText().toString()));
                                dbn.child("name").setValue(name.getText().toString());
                                dbn.child("quantity").setValue(qty.getText().toString());
                                dbn.child("description").setValue(desc.getText().toString());
                                dbn.child("catagory").setValue(spinner.getSelectedItem());
                                dbn.child("seller").setValue(prf.getString("UName",""));
                                dbn.child("rating").child("avgrating").setValue(0);
                                dbn.child("rating").child("people").setValue(0);
                                */
                                dbn.setValue(obj);
                                Uri uri=data.getData();
                                //FirebaseDatabase database = FirebaseDatabase.getInstance();
                                assert uri != null;
                                mstorage= FirebaseStorage.getInstance().getReference().child(uri.getLastPathSegment());
                                db.child("image").setValue(uri.getLastPathSegment());
                                dbn.child("image").setValue(uri.getLastPathSegment());
//            StorageReference filepath=mstorage.child("Login_Data").child(uri.getLastPathSegment());
                                mstorage.putBytes(fileInBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        upload.dismiss();
                                        Toast.makeText(getActivity(), "Done!", Toast.LENGTH_SHORT).show();

                                        name.setText("");
                                        qty.setText("");
                                        price.setText("");
                                        desc.setText("");
                                        spinner.setSelection(0);
                                        image.setImageDrawable(null);
                                    }
                                });
                            }
                            else{
                                    if (dataSnapshot.child(name.getText().toString()).child("seller").getValue(String.class).equals(prf.getString("UName","")))
                                    {
                                        String qty1=dataSnapshot.child(name.getText().toString()).child("quantity").getValue(String.class);
                                        assert qty1 != null;
                                        int q = Integer.parseInt(qty1);
                                        int i=Integer.parseInt(qty.getText().toString());
                                        q+=i;
                                        DatabaseReference db2=ref1.child("Posts").child(name.getText().toString());
                                        db2.child("quantity").setValue(String.valueOf(q));
                                        DatabaseReference dbn=ref1.child("Users").child(getActivity().getIntent().getStringExtra("UName")).child("MyItems").child(name.getText().toString());
                                        dbn.child("quantity").setValue(String.valueOf(q));
                                        Toast.makeText(getActivity(),"Item Already exist !! we are incrementing quantity!",Toast.LENGTH_LONG).show();
                                        name.setText("");
                                        qty.setText("");
                                        price.setText("");
                                        desc.setText("");
                                        spinner.setSelection(0);
                                        image.setImageDrawable(null);
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),"Product Name already exist please try with differrent name!",Toast.LENGTH_LONG).show();
                                    }
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_add_item,container,false);
    }
}