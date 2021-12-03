package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codekiller.fcsaadmin.Adapters.EbookAdapter;
import com.codekiller.fcsaadmin.Datas.EBookData;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EbookActivity extends AppCompatActivity {
    public static final int FILE_REQUEST_CODE = 100, FILE_RECEIVE_CODE = 123;
    public static final String TAG = "EBOOK ACTIVITY";

    FBUtils fbUtils;
    Utils utils;
    EBookData eBookData;
    EbookAdapter ebookAdapter;
    ArrayList<EBookData> arrayList;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    ImageView uploadBtn;
    DatabaseReference ebookDB;
    StorageReference ebookStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        uploadBtn = findViewById(R.id.upload_btn);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(100);

        fbUtils = new FBUtils();
        eBookData = new EBookData();
        utils = new Utils(this);
        arrayList = new ArrayList<>();

        ebookDB = fbUtils.getEbookDatabase();
        ebookStorage = fbUtils.getEbookStorage();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(EbookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                        ContextCompat.checkSelfPermission(EbookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EbookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(EbookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(EbookActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, FILE_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(EbookActivity.this, new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, FILE_REQUEST_CODE);
                    }
                } else {
                    uploadFile();
                }
            }
        });

        ebookDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                utils.showProgress("", "Loading");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayList.add(dataSnapshot.getValue(EBookData.class));
                }
                ebookAdapter = new EbookAdapter(EbookActivity.this, arrayList);
                recyclerView.setAdapter(ebookAdapter);
                utils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utils.dismissProgress();
                utils.toast("db failed");
            }
        });
    }

    private void uploadFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        Intent i = Intent.createChooser(intent, "File");
        startActivityForResult(i, FILE_RECEIVE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FILE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            uploadFile();
        } else {
            utils.toast("permission denied");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_RECEIVE_CODE && resultCode == RESULT_OK && data != null) {
            utils.toast("uploading");
            utils.initNotification("Uploading", true);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            StorageReference reference = ebookStorage.child(System.currentTimeMillis() + ".pdf");
            reference.putFile(data.getData())
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            utils.toast("uploaded");
                            progressBar.setVisibility(View.GONE);
                            String pushKey = ebookDB.push().getKey();
                            eBookData.setFilename(utils.getFileName(data.getData()));
                            Log.d(TAG, "onComplete: file name-" + utils.getFileName(data.getData()));
                            eBookData.setPushkey(pushKey);
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    eBookData.setDownloadUrl(uri + "");
                                    Log.d(TAG, "onComplete: url - " + uri);
                                    ebookDB.child(pushKey).setValue(eBookData)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    utils.toast("db failed");
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            int prog = (int) ((100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                            progressBar.setProgress(prog);
                            utils.setNotifyProgress(prog);
                            Log.d(TAG, "onProgress: " + (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.toast("failed");
                            progressBar.setVisibility(View.GONE);
                        }
                    });
//            new UploadProcess().execute(String.valueOf(data.getData()));
        }
    }

    // parameter, progress, result
    public class UploadProcess extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: uri - " + strings[0]);
            Uri uri = Uri.parse(strings[0]);
            StorageReference reference = ebookStorage.child(System.currentTimeMillis() + ".pdf");
            reference.putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            utils.toast("uploaded");
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            progressBar.setProgress((int) ((100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount()));
                            Log.d(TAG, "onProgress: " + (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            utils.toast("failed");
                        }
                    });
            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
//            utils.toast(s);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            progressBar.setProgress((values[0]));
        }
    }
}