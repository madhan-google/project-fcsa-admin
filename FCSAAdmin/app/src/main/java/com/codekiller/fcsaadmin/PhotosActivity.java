package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.codekiller.fcsaadmin.Adapters.PhotoAdapter;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PhotosActivity extends AppCompatActivity {
    public static final int IMAGE_REQUEST_CODE = 100, IMAGE_PERMISSION_CODE = 101;
    public static final String TAG = "ABOUT ACTIVITY";

    RecyclerView recyclerView;
    MaterialButton addBtn;
    ArrayList<HashMap<String, String>> arrayList;
    DatabaseReference photoDB;
    StorageReference storageReference;
    FBUtils fbUtils;
    Utils utils;
    PhotoAdapter photoAdapter;
    HashMap<String, String> map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        recyclerView = findViewById(R.id.recycler_view);
        addBtn = findViewById(R.id.add_btn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        fbUtils = new FBUtils();
        utils = new Utils(this);
        arrayList = new ArrayList<>();
        map = new HashMap<>();
        utils.showProgress("","Loading");
        photoDB = fbUtils.getPhotosDatabase();
        storageReference = fbUtils.getPhotoStorage();
        photoDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    arrayList.add((HashMap<String, String>) ds.getValue());
                }
                photoAdapter = new PhotoAdapter(PhotosActivity.this, arrayList);
                recyclerView.setAdapter(photoAdapter);
                utils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utils.dismissProgress();
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(PhotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PhotosActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, IMAGE_PERMISSION_CODE);
                }else{
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == IMAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null ){
            utils.showProgress("","Uploading");
            StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
            reference.putFile(data.getData())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String push_key = photoDB.push().getKey();
                                            map.put("push_key", push_key);
                                            map.put("download_url", uri+"");
                                            photoDB.child(push_key)
                                                    .setValue(map);
                                        }
                                    });
                            utils.dismissProgress();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.toast("failed");
                            utils.dismissProgress();
                        }
                    });
        }
    }
}