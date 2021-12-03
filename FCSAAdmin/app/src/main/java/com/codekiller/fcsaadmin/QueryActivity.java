package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.codekiller.fcsaadmin.Adapters.QueryAdapter;
import com.codekiller.fcsaadmin.Datas.QueryData;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QueryActivity extends AppCompatActivity {

    FBUtils fbUtils;
    Utils utils;
    QueryAdapter queryAdapter;

    RecyclerView recyclerView;
    ArrayList<QueryData> arrayList;

    DatabaseReference queryDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        recyclerView = findViewById(R.id.recycler_view);
        fbUtils = new FBUtils();
        utils = new Utils(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        queryDB = fbUtils.getQueryDatabase();
        arrayList = new ArrayList<>();
        utils.showProgress("","Loading");
        queryDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for( DataSnapshot dataSnapshot : snapshot.getChildren() ){
                    arrayList.add(dataSnapshot.getValue(QueryData.class));
                }
                queryAdapter = new QueryAdapter(QueryActivity.this, arrayList);
                recyclerView.setAdapter(queryAdapter);
                utils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utils.dismissProgress();
                utils.toast("loading failed");
            }
        });
    }
}