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
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codekiller.fcsaadmin.Adapters.EventAdapter;
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

public class EventActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MaterialButton addBtn;
    EditText eventText;
    ImageView imageView;

    DatabaseReference eventDB;
    StorageReference storageReference;

    FBUtils fbUtils;
    Utils utils;
    EventAdapter eventAdapter;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> map;
    String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        addBtn = findViewById(R.id.add_btn);
        eventText = findViewById(R.id.event_text);
        imageView = findViewById(R.id.image_view);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fbUtils = new FBUtils();
        utils = new Utils(this);
        arrayList = new ArrayList<>();
        map = new HashMap<>();

        eventDB = fbUtils.getEventDatabase();
        storageReference = fbUtils.getPhotoStorage();

        utils.showProgress("", "Loading");
        eventDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for( DataSnapshot ds : snapshot.getChildren() ){
                    arrayList.add((HashMap<String, String>) ds.getValue());
                }
                eventAdapter = new EventAdapter(EventActivity.this, arrayList);
                recyclerView.setAdapter(eventAdapter);
                utils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utils.dismissProgress();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(EventActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EventActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                }else{
                    startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 123);
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( eventText.getText().toString().trim().length() != 0 ){
                    String push_key = eventDB.push().getKey();
                    map.put("push_key", push_key);
                    map.put("event_message", eventText.getText().toString().trim());
                    map.put("download_url", downloadUrl.length()==0?"default":downloadUrl);
                    eventDB.child(push_key)
                            .setValue(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    utils.toast("failed");
                                }
                            });
                }else {
                    utils.toast("fill event message");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 123 && resultCode == RESULT_OK && data != null ){
            utils.showProgress("","Uploading");
            Glide.with(this)
                    .load(data.getData())
                    .fitCenter()
                    .into(imageView);
            StorageReference reference = storageReference.child(System.currentTimeMillis()+".jpg");
            reference.putFile(data.getData())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            reference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadUrl = uri+"";
                                            utils.dismissProgress();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.dismissProgress();
                            utils.toast("failed st");
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if( requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 123);
        }
    }
}