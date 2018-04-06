package com.example.redwan.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.LinkedHashMap;

public class RegesterActivity extends AppCompatActivity {

    private EditText user, email, pass, c_pass, varsityId;
    private Button button4;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regester);

        user = findViewById(R.id.user_id);
        email = findViewById(R.id.email_id);
        pass = findViewById(R.id.password);
        c_pass = findViewById(R.id.confirmPass);
        varsityId = findViewById(R.id.varsityId);

        button4 = findViewById(R.id.button4);

        mAuth = FirebaseAuth.getInstance();

        mProgress = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbarId_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String u = user.getText().toString();
            String e = email.getText().toString();
            String p = pass.getText().toString();
            String c_p = c_pass.getText().toString();
            String v_i = varsityId.getText().toString();
/*
            if(!TextUtils.isEmpty(u) && !TextUtils.isEmpty(e) && !TextUtils.isEmpty(p)) {
                if (p.compareTo(c_p) != 0) {
                    Toast.makeText(RegesterActivity.this, "Password is not Matched", Toast.LENGTH_LONG).show();
                }
                else {
                    regester_user(u, e, p);
                }
            }
            else{
                Toast.makeText(RegesterActivity.this, "something is missing", Toast.LENGTH_LONG).show();
            }
*/

// you need to be a daffodilian...

            if(!TextUtils.isEmpty(u) && !TextUtils.isEmpty(e) && !TextUtils.isEmpty(p) && !TextUtils.isEmpty(v_i)) {

                String name = u;
                Boolean check = false;
                for(int i = 0; i < name.length(); i++) {
                    if(!(name.charAt(i) >= 'a' && name.charAt(i) <= 'z') && !(name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') && !(name.charAt(i) == ' ')) {
                        check = true;
                        break;
                    }
                }

                String id = v_i;
                Boolean check1 = false;
                if(id.length() == 11) {
                    for (int i = 0; i < id.length(); i++) {
                        if(i < 3) {
                            if (!(id.charAt(i) >= '0' && id.charAt(i) <= '9')) {
                                check1 = true;
                                break;
                            }
                        }
                        else if(i == 3) {
                             if(!(id.charAt(i) == '-')){
                                 check1 = true;
                                 break;
                            }
                        }
                        else if(i < 6) {
                            if (!(id.charAt(i) >= '0' && id.charAt(i) <= '9')) {
                                check1 = true;
                                break;
                            }
                        }
                        else if(i == 6) {
                            if(!(id.charAt(i) == '-')){
                                check1 = true;
                                break;
                            }
                        }
                        else if(i < 11) {
                            if (!(id.charAt(i) >= '0' && id.charAt(i) <= '9')) {
                                check1 = true;
                                break;
                            }
                        }
                    }
                }
                else {
                    check1 = true;
                }

                try {
                    String substring = e.substring(e.length()-11, e.length());

                    if(!substring.equals("@diu.edu.bd")) {
                        Toast.makeText(RegesterActivity.this, "You Should be a Daffodilian. Please use Daffodil provide Email id.", Toast.LENGTH_LONG).show();
                    }
                    else if(check) {
                        Toast.makeText(RegesterActivity.this, "User name is not valid. use only A-Z, a-z or space", Toast.LENGTH_LONG).show();
                    }
                    else if(p.length() < 6 ) {
                            Toast.makeText(RegesterActivity.this, "Password Should be At least 6 characters", Toast.LENGTH_LONG).show();
                    }
                    else if (p.compareTo(c_p) != 0) {
                        Toast.makeText(RegesterActivity.this, "Password is not Matched", Toast.LENGTH_LONG).show();
                    }
                    else if(check1) {
                        Toast.makeText(RegesterActivity.this, "varsity id is not valid. Ex: 171-15-8557", Toast.LENGTH_LONG).show();
                    }
                    else {
                        regester_user(u, e, p, v_i);
                    }
                } catch (Exception ex) {
                    Toast.makeText(RegesterActivity.this, "You Should be a Daffodilian. Please use Daffodil provide Email id.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(RegesterActivity.this, "something is missing", Toast.LENGTH_LONG).show();
            }
            }
        });

    }
    public void regester_user(final String u, String e, String p, final String v_i) {

        mProgress.setMessage("Registering...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    LinkedHashMap<String, String> user = new LinkedHashMap<>();
                    user.put("Name", u);
                    user.put("Status", "Hi there, I'm using RaBe chat app");
                    user.put("Image", "default");   // https://firebasestorage.googleapis.com/v0/b/fir-chatapp-e2063.appspot.com/o/Profile%20picture%2F3xaEqAGgVAMOQv26aeUwOTslY8y2.jpg?alt=media&token=d289f042-c3df-4ced-b829-932940de47e0
                    user.put("Thumb_image", "default");
                    user.put("Id", v_i);
//                    user.put("Cover_picture", "default");
//                    user.put("Thumb_coverpic", "default");

                    databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                //        FirebaseAuth.getInstance().signOut();
                                Intent go = new Intent(RegesterActivity.this, MainActivity.class);
                                go.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);      // ber kore deoyar jonno
                                startActivity(go);
                                finish();
                            }
                        }
                    });
                }
                else {
                    mProgress.cancel();
                    Toast.makeText(RegesterActivity.this, "something is wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
