package com.codekiller.fcsaadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codekiller.fcsaadmin.Datas.QueryData;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.R;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {
    Context context;
    ArrayList<QueryData> arrayList;
    DatabaseReference queryDB;
    FBUtils fbUtils;

    public QueryAdapter(Context context, ArrayList<QueryData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        fbUtils = new FBUtils();
        queryDB = fbUtils.getQueryDatabase();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.query_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QueryData queryData = arrayList.get(position);
        holder.queryView.setText(queryData.getQuery());
        holder.nameView.setText(queryData.getName());
        holder.mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Reply from FCSA Academy");
                intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<b>Your Query:</b><br>" + queryData.getQuery() +
                        "<br><b>Answer:</b><br>"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{queryData.getMailId()});
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
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
                        .setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                queryDB.child(queryData.getPushkey()).removeValue();
                            }
                        })
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
        TextView nameView, queryView;
        ImageView mailBtn;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name_view);
            queryView = itemView.findViewById(R.id.query_view);
            mailBtn = itemView.findViewById(R.id.mail_btn);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
        }
    }
}
