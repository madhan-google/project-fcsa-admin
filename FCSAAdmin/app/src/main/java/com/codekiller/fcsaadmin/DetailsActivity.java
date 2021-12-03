package com.codekiller.fcsaadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codekiller.fcsaadmin.Datas.UserField;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.google.firebase.database.DatabaseReference;

public class DetailsActivity extends AppCompatActivity {
    int index;

    DatabaseReference candidateDB;
    TextView textView;
    ImageView callBtn, mailBtn;

    FBUtils fbUtils;
    UserField userField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fbUtils = new FBUtils();
        textView = findViewById(R.id.text_view);
        callBtn = findViewById(R.id.call_btn);
        mailBtn = findViewById(R.id.mail_btn);

        if( getIntent().getIntExtra("list_index", -1) != -1 ){
            userField = MainActivity.arrayList.get(getIntent().getIntExtra("list_index", -1));
        }else if( getIntent().getIntExtra("favorite_list_index", -1) != -1 ){
            userField = FavoriteActivity.list.get(getIntent().getIntExtra("favorite_list_index", -1));
        }
        String str = "<br><b>Name : </b>"+userField.getName()+"<br>" +
                "<br><b>Father Name : </b>"+userField.getFather_name()+"<br>" +
                "<br><b>Mother Name : </b>"+userField.getMother_name()+"<br>" +
                "<br><b>Date of Birth : </b>"+userField.getDob()+"<br>" +
                "<br><b>Registered Date and Time : </b>"+userField.getDate_time()+"<br>" +
                "<br><b>Class : </b>"+userField.getYour_class()+"<br>" +
                "<br><b>School Name : </b>"+userField.getCollege_name()+"<br>" +
                "<br><b>Board : </b>"+userField.getBoard()+"<br>" +
                "<br><b>Town : </b>"+userField.getTown()+"<br>" +
                "<br><b>District : </b>"+userField.getDistrict()+"<br>" +
                "<br><b>State : </b>"+userField.getState()+"<br>" +
                "<br><b>Opting Course : </b>"+userField.getOpting_course()+"<br>";
        textView.setText(Html.fromHtml(str));
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("tel:"+userField.getPh_no())));
            }
        });
        mailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "FCSA Academy");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{userField.getMail_id()});
                if( intent.resolveActivity(getPackageManager()) != null ){
                    startActivity(intent);
                }
            }
        });
    }
}