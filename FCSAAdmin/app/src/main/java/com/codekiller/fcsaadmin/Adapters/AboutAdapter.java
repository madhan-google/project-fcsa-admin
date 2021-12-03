package com.codekiller.fcsaadmin.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codekiller.fcsaadmin.AboutActivity;
import com.codekiller.fcsaadmin.Datas.AboutData;
import com.codekiller.fcsaadmin.EditActivity;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.codekiller.fcsaadmin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {
    public static final int CODE_REQUEST = 123;
    public static int posi = -1;

    Context context;
    public static ArrayList<AboutData> arrayList;

    FBUtils fbUtils;
    Utils utils;
    DatabaseReference aboutDB;
    StorageReference storageReference;
    AboutActivity activity;


    public AboutAdapter(Context context, ArrayList<AboutData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        fbUtils = new FBUtils();
        utils = new Utils(context);
        aboutDB = fbUtils.getAdminDatabase();
        storageReference = fbUtils.getAdminStorage();
        activity = new AboutActivity();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.about_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AboutData aboutData = arrayList.get(position);
        Glide.with(context)
                .load(aboutData.getDownload_url().equals("default") ? R.drawable.user_icon : aboutData.getDownload_url())
                .placeholder(R.drawable.login_as_user)
                .into(holder.userImage);
        holder.aboutView.setText(aboutData.getAbout_them());
        holder.subjectView.setText(aboutData.getSubject());
        holder.nameView.setText(aboutData.getName());
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, EditActivity.class)
                        .putExtra("posi", position));
            }
        });

        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete")
                        .setMessage("Are you sure")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!aboutData.getDownload_url().equals("default")) {
                                    FirebaseStorage.getInstance().getReferenceFromUrl(aboutData.getDownload_url()).delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                }
                                FirebaseDatabase.getInstance().getReference("Admin").child("About_Us").child(aboutData.getPush_key())
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                                utils.toast("deleted");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView nameView, subjectView, aboutView;
        ImageView editBtn;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            userImage = itemView.findViewById(R.id.user_image);
            nameView = itemView.findViewById(R.id.name_view);
            subjectView = itemView.findViewById(R.id.subject_view);
            aboutView = itemView.findViewById(R.id.about_view);
            editBtn = itemView.findViewById(R.id.save_btn);
        }
    }
}
