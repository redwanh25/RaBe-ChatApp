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
public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private View mainView;
    private DatabaseReference friendsDatabase;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = mainView.findViewById(R.id.friendsList_section);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();

        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("friends").child(uId);
        friendsDatabase.keepSynced(true);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        userDatabase.keepSynced(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter <Friends, FriendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class, R.layout.user_single_layout, FriendsViewHolder.class, friendsDatabase) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, final Friends model, int position) {

                viewHolder.setDate(model.getDate());

                final String userKey = getRef(position).getKey();

                userDatabase.child(userKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String name = dataSnapshot.child("Name").getValue().toString();
                        String thumbPic = dataSnapshot.child("Thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("Online")) {
                            Boolean pic = (boolean) dataSnapshot.child("Online").getValue();
                            viewHolder.setOnlineStatus(pic);
                        }

                        viewHolder.setName(name);
                        viewHolder.setImage(thumbPic, getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String[] option = {"Open Profile", "Send Message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if(which == 0) {
                                            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                            profileIntent.putExtra("key", userKey);
                                            startActivity(profileIntent);
                                        }
                                        else if(which == 1) {
                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                            chatIntent.putExtra("key", userKey);
                                            chatIntent.putExtra("user_name", name);
                                            startActivity(chatIntent);
                                        }

                                    }
                                });
                                builder.show();

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

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date) {
            TextView time = mView.findViewById(R.id.status_allUsers);
            time.setText(date);
        }
        public void setName(String name) {
            TextView displayName = mView.findViewById(R.id.display_name_allUsers);
            displayName.setText(name);
        }
        public void setImage(final String image, final Context context) {
            final CircleImageView thumImage = mView.findViewById(R.id.proPic_allUsers);

            Picasso.with(context).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.avatar_default).into(thumImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(image).placeholder(R.drawable.avatar_default).into(thumImage);
                }
            });
        }
        public void setOnlineStatus(Boolean pic) {
            ImageView image = mView.findViewById(R.id.onLine);

            if(pic.equals(true)) {
                image.setVisibility(View.VISIBLE);
            }
            else {
                image.setVisibility(View.INVISIBLE);
            }

        }
    }

}
