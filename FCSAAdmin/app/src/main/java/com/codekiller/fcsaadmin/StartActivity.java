package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    FBUtils fbUtils;
    Utils utils;

    FirebaseAuth auth;

    EditText mailText, passText;
    Button loginBtn;
    TextView forgotBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mailText = findViewById(R.id.mailid_text);
        passText = findViewById(R.id.pass_text);
        loginBtn = findViewById(R.id.login_btn);
        forgotBtn = findViewById(R.id.forgot_view);

        fbUtils = new FBUtils();
        utils = new Utils(this);
        auth = fbUtils.getAuth();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.showProgress("Logging in..", "Please wait");
                if( mailText.getText().toString().length() != 0 && passText.getText().toString().length() != 0 ){
                    auth.signInWithEmailAndPassword(mailText.getText().toString().trim(), passText.getText().toString().trim())
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    utils.toast("login failed");
                                    utils.dismissProgress();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    utils.dismissProgress();
                                    startActivity(new Intent(StartActivity.this, MainActivity.class));
                                }
                            });
                }else{
                    utils.toast("fill all the fields");
                }
            }
        });

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, ForgotActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if( auth.getCurrentUser() != null ){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}