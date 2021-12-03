package com.codekiller.fcsaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.codekiller.fcsaadmin.Datas.UserField;
import com.codekiller.fcsaadmin.DetailsActivity;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.codekiller.fcsaadmin.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.ViewHolder> {
    Context context;
    ArrayList<UserField> arrayList;
    DatabaseReference favoriteDB, candidateDB;
    FBUtils fbUtils;
    Utils utils;

    String fromWhere;

    public CandidatesAdapter(Context context, ArrayList<UserField> arrayList, String fromWhere) {
        this.context = context;
        this.arrayList = arrayList;
        this.fromWhere = fromWhere;
        fbUtils = new FBUtils();
        favoriteDB = fbUtils.getFavoritesDatabase();
        candidateDB = fbUtils.getCandidateDatabase();
        utils = new Utils(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.candidate_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserField userField = arrayList.get(position);
        holder.numberView.setText(userField.getPh_no()+"");
        holder.classView.setText(userField.getYour_class());
        holder.nameView.setText(userField.getName());
        holder.dateView.setText(userField.getDate_time());
        if( userField.isFavorite() ){
            holder.favoriteBtn.setVisibility(View.GONE);
        }else{
            holder.favoriteBtn.setVisibility(View.VISIBLE);
        }
        if( !userField.isSeen() ){
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.purple_200));
        }else{
            holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userField.setSeen(true);
                holder.relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
                candidateDB.child(userField.getPushKey()).setValue(userField);
//                notifyDataSetChanged();
                context.startActivity(new Intent(context, DetailsActivity.class)
                .putExtra(fromWhere.equals("main")?"list_index":"favorite_list_index", position));
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
                                userField.setFavorite(false);
                                if( fromWhere.equals("main") ){
                                    candidateDB.child(userField.getPushKey()).removeValue();
                                }else{
                                    favoriteDB.child(userField.getFavoritePushkey()).removeValue();
                                }
                                utils.toast("Record Deleted");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                userField.setSeen(true);
                userField.setFavorite(true);
                String favPushkey = favoriteDB.push().getKey();
                userField.setFavoritePushkey(favPushkey);
                favoriteDB.child(favPushkey).setValue(userField);
                candidateDB.child(userField.getPushKey()).setValue(userField);
                holder.favoriteBtn.setVisibility(View.GONE);
//                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout relativeLayout;
        TextView nameView, classView, numberView, dateView;
        ImageView favoriteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            nameView = itemView.findViewById(R.id.name_view);
            classView = itemView.findViewById(R.id.class_view);
            numberView = itemView.findViewById(R.id.number_view);
            favoriteBtn = itemView.findViewById(R.id.favorite_btn);
            dateView = itemView.findViewById(R.id.date_view);
        }
    }
}
