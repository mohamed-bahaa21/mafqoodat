package com.project.firebasesocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt, mNameEt, mPhoneEt;
    Button mRegisterBtn;
    TextView mHaveAccountTv;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        actionbar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
//        enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mNameEt = findViewById(R.id.nameEt);
        mPhoneEt = findViewById(R.id.phoneEt);

        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        mAuth = FirebaseAuth.getInstance();


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String name = mNameEt.getText().toString().trim();
                String phone = mPhoneEt.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mEmailEt.setError("Invalid Email");
                } else if (password.length() < 6){
                    mPasswordEt.setError("Password Length at least 6 charachters");
                } else if(name.length() < 3){
                    mNameEt.setError("Name Length at least 3 charachters");
                } else if(!Patterns.PHONE.matcher(phone).matches()){
                    mPhoneEt.setError("Invalid Phone");
                }
                else {
                    registerUser(email, password, name, phone);
                }
            }
        });

        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password, final String name, final String phone) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    progressDialog.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();

                    // get user email & uid from auth
                    String email = user.getEmail();
                    String uid = user.getUid();
                    //when user is registered store user info in firebase realtime database too using hashmap
                    HashMap<Object, String> hashMap = new HashMap<>();
                    // put info in hashmap
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", name); // will add later (e.g. edit profile)
                    hashMap.put("onlineStatus", "online"); // will add later (e.g. edit profile)
                    hashMap.put("typingTo", "noOne"); // will add later (e.g. edit profile)
                    hashMap.put("phone", phone); // will add later (e.g. edit profile)
                    hashMap.put("image", ""); // will add later (e.g. edit profile)
                    hashMap.put("cover", ""); // will add later (e.g. edit profile)

                    //firebase database instance
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    // path to store user data named "Users"
                    DatabaseReference reference = database.getReference("Users");
                    //put data within hashmap in database
                    reference.child(uid).setValue(hashMap);


                    Toast.makeText(RegisterActivity.this, "Registered... \n"+user.getEmail(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                } else {
                    // If sign in fails, display a message to the user.
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // go previous activity
        return super.onSupportNavigateUp();
    }
}
