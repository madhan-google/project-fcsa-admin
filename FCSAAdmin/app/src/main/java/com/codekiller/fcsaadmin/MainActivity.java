package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import com.codekiller.fcsaadmin.Adapters.CandidatesAdapter;
import com.codekiller.fcsaadmin.Datas.UserField;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference candidates;
    RecyclerView recyclerView;
    EditText searchText;
    CandidatesAdapter candidatesAdapter;
    FBUtils fbUtils;
    public static ArrayList<UserField> arrayList;
    Utils utils;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        searchText = findViewById(R.id.search_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fbUtils = new FBUtils();
        utils = new Utils(this);
        utils.showProgress("", "Loading");
        candidates = fbUtils.getCandidateDatabase();
        arrayList = new ArrayList<>();
        initDatas();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateList(s.toString());
            }
        });
    }

    private void initDatas() {
        candidates.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayList.add(dataSnapshot.getValue(UserField.class));
                }
                candidatesAdapter = new CandidatesAdapter(MainActivity.this, arrayList, "main");
                recyclerView.setAdapter(candidatesAdapter);
                utils.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                utils.dismissProgress();
            }
        });
    }

    public void updateList(String s) {
        ArrayList<UserField> temp = new ArrayList<>();
        for (UserField userField : arrayList) {
            if (userField.getName().contains(s)) {
                temp.add(userField);
            }
        }
        candidatesAdapter = new CandidatesAdapter(this, temp, "main");
        recyclerView.setAdapter(candidatesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                fbUtils.getAuth().signOut();
                finish();
                break;
            case R.id.favorites:
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                break;
            case R.id.ebook:
                startActivity(new Intent(MainActivity.this, EbookActivity.class));
                break;
            case R.id.about_us:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.query:
                startActivity(new Intent(MainActivity.this, QueryActivity.class));
                break;
            case R.id.photos:
                startActivity(new Intent(MainActivity.this, PhotosActivity.class));
                break;
            case R.id.events:
                startActivity(new Intent(MainActivity.this, EventActivity.class));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        initDatas();
    }
}