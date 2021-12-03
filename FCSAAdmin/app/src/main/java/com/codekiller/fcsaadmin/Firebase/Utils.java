package com.codekiller.fcsaadmin.Firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.codekiller.fcsaadmin.EbookActivity;
import com.codekiller.fcsaadmin.R;

public class Utils {
    Context context;
    ProgressDialog progressDialog;
    NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    public Utils(Context context) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public void toast(String s){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
    public void showProgress(String title, String message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    public void dismissProgress(){
        progressDialog.dismiss();
    }
    public String getFileName(Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
    }
    public void initNotification(String title, boolean isUpload){
        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(!isUpload ? R.drawable.download : R.drawable.upload)
                .setContentTitle(title)
                .setAutoCancel(false);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("notify", "Notification", NotificationManager.IMPORTANCE_HIGH);
            builder.setChannelId("notify");
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, builder.build());
    }
    public void setNotifyProgress(int progress){
        if( progress == 100 ){
            notificationManager.cancel(0);
        }
    }
}
