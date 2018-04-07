package com.example.redwan.firebasechatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullScreenImageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image_view);
        ImageView imageView = findViewById(R.id.image_fullScreen);

        Intent intent = getIntent();
        String call = intent.getType();

        Picasso.with(this).load(call).placeholder(R.drawable.avatar_default).into(imageView);
    }


}
