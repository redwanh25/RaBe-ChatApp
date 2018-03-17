package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 100;
    private FirebaseUser mFireBaseUser;
    private DatabaseReference mDatabaseReference;
    private TextView display;
    private TextView c_status;
//    private CircleImageView proPic;
    private ImageView proPic;
    private ImageView coverPic, coverPicButton;
    private Button changeStatus, changePic;
    private static final int GALLERY_PIC = 1;
    private StorageReference mStorage;
    private ProgressDialog mProgressBar;
//   private String pressed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = mFireBaseUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uId);

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgressBar = new ProgressDialog(this);

        display = findViewById(R.id.setting_displayName);
        c_status = findViewById(R.id.setting_status);
        proPic = findViewById(R.id.setting_image);
        changeStatus = findViewById(R.id.setting_changeStatusButton);
        changePic = findViewById(R.id.setting_changeImageButton);
//        coverPic = findViewById(R.id.setting_coverPic);
//        coverPicButton = findViewById(R.id.setting_coverPicButton);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue().toString();
                String image = dataSnapshot.child("Image").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String thumbImage = dataSnapshot.child("Thumb_image").getValue().toString();
//                String coverPicture = dataSnapshot.child("Cover_picture").getValue().toString();
//                String thumbCover = dataSnapshot.child("Thumb_coverpic").getValue().toString();

                display.setText(name);
                c_status.setText(status);

                if(!thumbImage.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(thumbImage).placeholder(R.drawable.avatar_default).into(proPic);

//                    Picasso.with(SettingsActivity.this)
//                            .load(thumbImage)
//                            .placeholder(R.drawable.avatar_default)
//                            .resize(200, 200)
//                            .centerCrop()
//                            .into(proPic);
                }
//                if(!thumbCover.equals("default")) {
//                    Picasso.with(SettingsActivity.this).load(thumbCover).placeholder(R.drawable.avater_coverpic).into(coverPic);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangeStatus.class));
            }
        });

        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PIC);

//                pressed = "PROFILE PIC";

//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingsActivity.this);

            }
        });

//        coverPicButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent gallery = new Intent();
//                gallery.setType("image/*");
//                gallery.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), GALLERY_PIC);
//                pressed = "COVER PIC";
////                CropImage.activity()
////                        .setGuidelines(CropImageView.Guidelines.ON)
////                        .start(SettingsActivity.this);
//
//            }
//        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

//        if(pressed.equals("PROFILE PIC")){

            if(requestCode == GALLERY_PIC && resultCode == RESULT_OK){
                Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)       // setAspectRatio(1, 1) aita hosse pic k square kore crop korar jonno.
               //         .setMinCropWindowSize(200, 200)       // ai line dile code crash kore
                        .start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    mProgressBar.setTitle("Uploading Image");
                    mProgressBar.setMessage("Please wait while we uploading the image and process...");
                    mProgressBar.setCanceledOnTouchOutside(false);
                    mProgressBar.show();

                    Uri resultUri = result.getUri();  // finally jei pic ta gallery theke crop kore processing hoye ashlo shei pic er Uri. aita akhun upload hobe.
                    String uId = mFireBaseUser.getUid();

                    File thumb_filePath = new File(resultUri.getPath());
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(10)
                            .compressToBitmap(thumb_filePath);

                    //for uploading thumb image to database
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();

                    StorageReference store = mStorage.child("Profile picture").child(uId + ".jpg");     // random() er bodole "uId" likhle valo hobe.
                    final StorageReference thumb_store = mStorage.child("Profile picture").child("Thumb").child(uId + ".jpg");

                    store.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()){

                                final String download_url = task.getResult().getDownloadUrl().toString();

                                UploadTask uploadTask = thumb_store.putBytes(thumb_byte);

                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                        String download_thumb_url = thumb_task.getResult().getDownloadUrl().toString();

                                        if(thumb_task.isSuccessful()) {

                                            Map updateHashMap = new HashMap();      //    Map <String, String> likhle hobe na.
                                            updateHashMap.put("Image", download_url);
                                            updateHashMap.put("Thumb_image", download_thumb_url);

                                            mDatabaseReference.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()) {
                                                        mProgressBar.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
                                                    }
                                                    else{
                                                        mProgressBar.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                        else{

                                        }
                                    }
                                });
                            }
                            else{
                                mProgressBar.dismiss();
                                Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

//        }
//        else if(pressed.equals("COVER PIC")){
//
//            if(requestCode == GALLERY_PIC && resultCode == RESULT_OK){
//                Uri imageUri = data.getData();
//                CropImage.activity(imageUri)
//                        .setAspectRatio(40, 25)       // setAspectRatio(1, 1) aita hosse pic k square kore crop korar jonno.
//                        //         .setMinCropWindowSize(200, 200)       // ai line dile code crash kore
//                        .start(this);
//            }
//
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//                if (resultCode == RESULT_OK) {
//
//                    mProgressBar.setTitle("Uploading Image");
//                    mProgressBar.setMessage("Please wait while we uploading the image and process...");
//                    mProgressBar.setCanceledOnTouchOutside(false);
//                    mProgressBar.show();
//
//                    Uri resultUri = result.getUri();  // finally jei pic ta gallery theke crop kore processing hoye ashlo shei pic er Uri. aita akhun upload hobe.
//                    String uId = mFireBaseUser.getUid();
//
//                    File thumb_filePath = new File(resultUri.getPath());
//                    Bitmap thumb_bitmap = new Compressor(this)
//                            .setMaxWidth(358)
//                            .setMaxHeight(224)
//                            .setQuality(75)
//                            .compressToBitmap(thumb_filePath);
//
//                    //for uploading thumb image to database
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    final byte[] thumb_byte = baos.toByteArray();
//
//                    StorageReference store = mStorage.child("Cover picture").child(uId + ".jpg");     // random() er bodole "uId" likhle valo hobe.
//                    final StorageReference thumb_store = mStorage.child("Cover picture").child("Thumb").child(uId + ".jpg");
//
//                    store.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if(task.isSuccessful()){
//
//                                final String download_url = task.getResult().getDownloadUrl().toString();
//
//                                UploadTask uploadTask = thumb_store.putBytes(thumb_byte);
//
//                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
//
//                                        String download_thumb_url = thumb_task.getResult().getDownloadUrl().toString();
//
//                                        if(thumb_task.isSuccessful()) {
//
//                                            Map updateHashMap = new HashMap();      //    Map <String, String> likhle hobe na.
//                                            updateHashMap.put("Cover_picture", download_url);
//                                            updateHashMap.put("Thumb_coverpic", download_thumb_url);
//
//                                            mDatabaseReference.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if(task.isSuccessful()) {
//                                                        mProgressBar.dismiss();
//                                                        Toast.makeText(SettingsActivity.this, "Uploaded", Toast.LENGTH_LONG).show();
//                                                    }
//                                                    else{
//                                                        mProgressBar.dismiss();
//                                                        Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
//                                                    }
//                                                }
//                                            });
//                                        }
//                                        else{
//
//                                        }
//                                    }
//                                });
//                            }
//                            else{
//                                mProgressBar.dismiss();
//                                Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    });
//
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                    Exception error = result.getError();
//                }
//            }
//
//       }
    }

/*    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 1 + generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(75) + 48);
            randomStringBuilder.append(tempChar);
        }

        StringBuilder randomStringBuilder1 = new StringBuilder();
        char tempChar1;
        for (int i = 0; i < randomLength; i++){
            tempChar1 = (char) (generator.nextInt(75) + 48);
            randomStringBuilder1.append(tempChar1);
            randomStringBuilder1.append(randomStringBuilder.charAt(i));
        }
        return randomStringBuilder1.toString();
    }*/

}
