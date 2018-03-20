package com.example.redwan.firebasechatapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by redwan on 18-Mar-18.
 */

public class OfflineCapabilities extends Application {

    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // --------- Picasso for Image offline capabilities ---------- //

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


        //------------------ online status --------------------- //

//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        String uId = currentUser.getUid();
//
//        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uId);
//
//        userDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot != null) {
//
//                    userDatabase.child("Online").onDisconnect().setValue(false);
//                    userDatabase.child("Online").setValue(true);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

}
