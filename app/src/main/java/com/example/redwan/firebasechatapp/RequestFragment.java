package com.example.redwan.firebasechatapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView recyclerView;
    private View mainView;
    private DatabaseReference requestDatabase;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainView = inflater.inflate(R.layout.fragment_request, container, false);

        recyclerView = mainView.findViewById(R.id.friendsRequest_section);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();

        requestDatabase = FirebaseDatabase.getInstance().getReference().child("friend_request").child(uId);
        requestDatabase.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                Request.class, R.layout.user_single_layout_request, RequestViewHolder.class, requestDatabase) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, final Request model, int position) {

                final String userKey = getRef(position).getKey();

                userDatabase.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("Name").getValue().toString();
                        String thumbPic = dataSnapshot.child("Thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("Online")) {
                            Boolean pic = (boolean) dataSnapshot.child("Online").getValue();
                            viewHolder.setOnlineStatus(pic);
                        }
                        viewHolder.setImage(thumbPic, getContext());
                        viewHolder.setDisplayName(name);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("key", userKey);
                                startActivity(profileIntent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDisplayName(String name){
            TextView mTextView = mView.findViewById(R.id.request_displayName);
            mTextView.setText(name);
        }

        public void setImage(final String thumb, final Context cntx) {
            final CircleImageView img = mView.findViewById(R.id.request_profilePic);
//          Picasso.with(cntx).load(thumb).placeholder(R.drawable.avatar_default).into(img);

            Picasso.with(cntx).load(thumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(cntx).load(thumb).placeholder(R.drawable.avatar_default).into(img);
                }
            });
        }

        public void setOnlineStatus(Boolean pic) {
            ImageView image = mView.findViewById(R.id.onLineChat);

            if(pic.equals(true)) {
                image.setVisibility(View.VISIBLE);
            }
            else {
                image.setVisibility(View.INVISIBLE);
            }

        }
    }

}
