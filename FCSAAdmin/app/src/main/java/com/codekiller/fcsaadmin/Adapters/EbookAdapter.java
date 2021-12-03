package com.codekiller.fcsaadmin.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.codekiller.fcsaadmin.Datas.EBookData;
import com.codekiller.fcsaadmin.EbookActivity;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.codekiller.fcsaadmin.MainActivity;
import com.codekiller.fcsaadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.codekiller.fcsaadmin.EbookActivity.FILE_REQUEST_CODE;
import static com.codekiller.fcsaadmin.EbookActivity.TAG;

public class EbookAdapter extends RecyclerView.Adapter<EbookAdapter.ViewHolder> {

    Context context;
    ArrayList<EBookData> arrayList;
    StorageReference storageReference;
    DatabaseReference ebookDB;
    FBUtils fbUtils;
    Utils utils;
    EbookActivity ebookActivity;

    public EbookAdapter(Context context, ArrayList<EBookData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        fbUtils = new FBUtils();
        utils = new Utils(context);
        ebookDB = fbUtils.getEbookDatabase();
        ebookActivity = new EbookActivity();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ebook_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EBookData eBookData = arrayList.get(position);
        holder.nameView.setText(eBookData.getFilename());
        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ){
                    ActivityCompat.requestPermissions((Activity) context, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },100);
                }else {
                    utils.toast("downloading");
                    utils.initNotification("Downloading", false);
//                https://firebasestorage.googleapis.com/v0/b/fcsa-academy.appspot.com/o/eBooks%2FExt_50937.pdf?alt=media&token=a95bc322-8212-4e4e-ad30-b1396b3a6ae3
                    File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                    File dir = new File(f, "FCSA Academy Notes/" + eBookData.getFilename());
                    if (!dir.exists()) {
                        try {
                            dir.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "onClick: url - " + eBookData.getDownloadUrl());
                    storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(eBookData.getDownloadUrl());
                    storageReference.getFile(dir)
                            .addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                    utils.toast("downloaded successfully\n" + dir);
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                    utils.setNotifyProgress((int) ((snapshot.getBytesTransferred() * 100) / snapshot.getTotalByteCount()));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    utils.toast("download failed");
                                }
                            });
                }
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Are you sure")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ebookDB.child(eBookData.getPushkey()).removeValue();
                                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(eBookData.getDownloadUrl());
                                storageReference.delete();
                                utils.toast("deleted");
                            }
                        })
                        .setNegativeButton("no",null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        ImageView downloadBtn;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.file_name);
            downloadBtn = itemView.findViewById(R.id.download_btn);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }
}

    /*// Create a reference with an initial file path and name
    StorageReference pathReference = storageRef.child("images/stars.jpg");

     Create a reference to a file from a Cloud Storage URI
    StorageReference gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg");

     Create a reference from an HTTPS URL
    Note that in the URL, characters are URL escaped!
    StorageReference httpsReference = storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg");*/