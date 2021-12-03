package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity {

    FBUtils fbUtils;
    Utils utils;

    FirebaseAuth auth;

    EditText mailText;
    Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mailText = findViewById(R.id.mailid_text);
        sendBtn = findViewById(R.id.send_btn);

        fbUtils = new FBUtils();
        utils = new Utils(this);

        auth = fbUtils.getAuth();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.showProgress("Sending Request", "");
                if( mailText.getText().toString().trim().length() != 0 ) {
                    auth.sendPasswordResetEmail(mailText.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    utils.toast("check your mail");
                                    utils.dismissProgress();
                                }
                            })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    utils.dismissProgress();
                                    finish();
                                }
                            });
                }
            }
        });
    }
}