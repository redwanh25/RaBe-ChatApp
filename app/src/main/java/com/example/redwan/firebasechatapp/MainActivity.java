package com.example.redwan.firebasechatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionPagerAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference onlineDatabase;
    private FirebaseUser current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbarId_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("RaBe Conversation App");

        // Tabs
        mViewPager = findViewById(R.id.main_tabPager);
        mSectionPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            setToStart();
        }
        else {

            // online -------------------------

            current = FirebaseAuth.getInstance().getCurrentUser();
            onlineDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current.getUid()).child("Online");
            onlineDatabase.setValue("true");
        }
    }

    // online -------------------------

    @Override
    public void onResume() {
        super.onResume();
        onlineDatabase.setValue("true");
    }

    @Override
    public void onPause() {
        super.onPause();
        onlineDatabase.setValue(ServerValue.TIMESTAMP);
    }

    // online -------------------------

    private void setToStart(){
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.accountSetting){
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        else if(item.getItemId() == R.id.changePass){
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }
        else if(item.getItemId() == R.id.delete_account){
            startActivity(new Intent(MainActivity.this, DeleteAccountActivity.class));
        }
        else if(item.getItemId() == R.id.logout){
            onlineDatabase.setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();
            setToStart();
        }
        else if(item.getItemId() == R.id.all_users){
            startActivity(new Intent(MainActivity.this, AllUsersActivity.class));
        }
        return true;
    }
}