package com.example.redwan.firebasechatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);

        toolbar = findViewById(R.id.toolbarId_allUsers);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        recyclerView = findViewById(R.id.all_Users_RecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // realtime  niye kdatabaseaj korle "onStart()" method likhte hobe.
    // Firebase UI
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter <Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class, R.layout.user_single_layout, UsersViewHolder.class, mDatabaseReference) {
            @Override
            protected void populateViewHolder(UsersViewHolder viewHolder, Users model, int position) {
                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.set_thumbImage(model.getThumb_image(), getApplicationContext());

                final String userKey = getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(AllUsersActivity.this, Profile.class);
                        profileIntent.putExtra("key", userKey);
                        startActivity(profileIntent);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView mTextView = mView.findViewById(R.id.display_name_allUsers);
            mTextView.setText(name);
        }
        public void setUserStatus(String status){
            TextView mTextView = mView.findViewById(R.id.status_allUsers);
            mTextView.setText(status);
        }
        public void set_thumbImage(String thumb, Context cntx) {
            CircleImageView img = mView.findViewById(R.id.proPic_allUsers);
            Picasso.with(cntx).load(thumb).placeholder(R.drawable.avatar_default).into(img);
        }
    }
}
