package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.codekiller.fcsaadmin.Adapters.CandidatesAdapter;
import com.codekiller.fcsaadmin.Datas.UserField;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FBUtils fbUtils;
    Utils utils;
    DatabaseReference favoriteDB, candidateDB;
    CandidatesAdapter candidatesAdapter;
    public static ArrayList<UserField> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fbUtils = new FBUtils();
        utils = new Utils(this);
        favoriteDB = fbUtils.getFavoritesDatabase();
        candidateDB = fbUtils.getCandidateDatabase();
        list = new ArrayList<>();
        utils.showProgress("", "Loading");
        favoriteDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for( DataSnapshot dataSnapshot :snapshot.getChildren() ){
                    list.add(dataSnapshot.getValue(UserField.class));
                }

                candidatesAdapter = new CandidatesAdapter(FavoriteActivity.this, list, "favorite");
                recyclerView.setAdapter(candidatesAdapter);
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