package com.appsnipp.profiledesigns.ui.feedback;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.profiledesigns.AdaptorClasses.User;
import com.appsnipp.profiledesigns.EmailJavaFiles.SendMail;
import com.appsnipp.profiledesigns.Login_SignUpFiles.LogIn;
import com.appsnipp.profiledesigns.R;
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

public class feedbackFragment extends Fragment {
public void onViewCreated(final View view, Bundle savedInstanceState)
{
    super.onViewCreated(view,savedInstanceState);
    final EditText feedback=(EditText)view.findViewById(R.id.feedback);
    ImageButton send =(ImageButton)view.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (!feedback.getText().toString().equals(""))
            {
                final AlertDialog.Builder alertnameedit = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View newview = factory.inflate(R.layout.feedbackdialogue, null);
                alertnameedit.setTitle("AgriTech says : ");
                alertnameedit.setView(newview);
                final AlertDialog dlg= alertnameedit.show();
                DatabaseReference dbr=FirebaseDatabase.getInstance().getReference().child("Feedbacks").push();
                dbr.child("Feedback").setValue(feedback.getText().toString());
                dbr.child("Name").setValue(getActivity().getIntent().getStringExtra("UName"));
                feedback.setText("");
            }
            else
            {
                Toast.makeText(getActivity(),"Write Something !",Toast.LENGTH_SHORT).show();
            }

        }
    });
}

public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_feedback,container,false);
    }
}