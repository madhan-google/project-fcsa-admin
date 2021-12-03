package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.codekiller.fcsaadmin.Adapters.AboutAdapter;
import com.codekiller.fcsaadmin.Datas.AboutData;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {
    public static final int IMAGE_PERMISSION = 10001, IMAGE_REQUEST = 1234;
    public static final String TAG = "EDIT ACTIVITY";

    MaterialButton updateBtn, removeBtn;
    EditText nameText, subjectText, aboutText;
    CircleImageView imageView;

    AboutData aboutData;
    StorageReference storageReference;
    DatabaseReference adminDB;
    FBUtils fbUtils;
    Utils utils;

    String downloadUrl = "", name, subject, about, push_key = "";
    int posi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        updateBtn = findViewById(R.id.upload_btn);
        nameText = findViewById(R.id.name_text);
        subjectText = findViewById(R.id.subject_text);
        aboutText = findViewById(R.id.about_text);
        imageView = findViewById(R.id.image_view);
        removeBtn = findViewById(R.id.remove_btn);

        fbUtils = new FBUtils();
        utils = new Utils(this);
        storageReference = fbUtils.getAdminStorage();
        adminDB = fbUtils.getAdminDatabase();

        posi = getIntent().getIntExtra("posi", -1);
        aboutData = new AboutData();
        if (posi != -1) {
            aboutData = AboutAdapter.arrayList.get(posi);
            nameText.setText(aboutData.getName());
            subjectText.setText(aboutData.getSubject());
            aboutText.setText(aboutData.getAbout_them());
            Log.d(TAG, "onCreate: download url - " + aboutData.getDownload_url());
            Glide.with(this)
                    .load(aboutData.getDownload_url().equals("default") ? R.drawable.user_icon : aboutData.getDownload_url())
                    .placeholder(R.drawable.login_as_user)
                    .into(imageView);
        } else {
            updateBtn.setText("ADD");
        }

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(EditActivity.this)
                        .load(R.drawable.user_icon)
                        .into(imageView);
                FirebaseStorage.getInstance().getReferenceFromUrl(aboutData.getDownload_url()).delete();
                aboutData.setDownload_url("default");
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, IMAGE_PERMISSION);
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_REQUEST);
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (posi != -1) {
                    if (!aboutData.getDownload_url().equals("default")) {
                        FirebaseStorage.getInstance().getReferenceFromUrl(aboutData.getDownload_url()).delete();
                    }
                }
                Log.d(TAG, "onClick: dow url - " + downloadUrl);
                name = nameText.getText().toString().trim();
                subject = subjectText.getText().toString().trim();
                about = aboutText.getText().toString().trim();
                if (name.length() != 0 && subject.length() != 0 && about.length() != 0) {
                    if (downloadUrl.length() != 0) {
                        aboutData.setDownload_url(downloadUrl);
                    } else {
                        aboutData.setDownload_url("default");
                    }
                    aboutData.setAbout_them(about);
                    aboutData.setName(name);
                    aboutData.setSubject(subject);
                    push_key = posi == -1 ? adminDB.push().getKey() : aboutData.getPush_key();
                    aboutData.setPush_key(push_key);
                    adminDB.child("About_Us")
                            .child(push_key)
                            .setValue(aboutData)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    utils.toast("failed");
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    utils.toast(posi == -1 ? "added" : "updated");
                                    finish();
                                }
                            });
                } else {
                    utils.toast("fill all the fields");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Glide.with(this)
                    .load(data.getData())
                    .placeholder(R.drawable.login_as_user)
                    .into(imageView);
            utils.showProgress("", "Uploading");
            StorageReference reference = storageReference.child(System.currentTimeMillis() + ".jpg");
            reference.putFile(data.getData())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl = uri + "";
                                            utils.dismissProgress();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            utils.dismissProgress();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.dismissProgress();
                        }
                    });
        }
    }
}