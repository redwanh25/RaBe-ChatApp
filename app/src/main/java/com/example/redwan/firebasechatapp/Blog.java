package com.example.redwan.firebasechatapp;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Blog extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    ConstraintLayout constraintLayout;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        bottomNavigation = findViewById(R.id.bottomNavigationView);

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        replaceFragment(homeFragment);

        constraintLayout = findViewById(R.id.myLayout_blog);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navigation_home) {
                    replaceFragment(homeFragment);
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_search) {
                    replaceFragment(searchFragment);
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_notification) {
                    replaceFragment(notificationFragment);
                    return true;
                }
                else if(item.getItemId() == R.id.navigation_account) {
                    replaceFragment(accountFragment);
                    return true;
                }
                else return false;
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.blog_container, fragment);
        fragmentTransaction.commit();
    }
}
