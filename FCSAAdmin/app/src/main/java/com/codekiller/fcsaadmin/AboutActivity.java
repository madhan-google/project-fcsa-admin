package com.codekiller.fcsaadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codekiller.fcsaadmin.Adapters.AboutAdapter;
import com.codekiller.fcsaadmin.Datas.AboutData;
import com.codekiller.fcsaadmin.Firebase.FBUtils;
import com.codekiller.fcsaadmin.Firebase.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.codekiller.fcsaadmin.Adapters.AboutAdapter.posi;

public class AboutActivity extends AppCompatActivity {
    public static final int IMAGE_REQUEST_CODE = 100, IMAGE_PERMISSION_CODE = 101;
    public static final String TAG = "ABOUT ACTIVITY";
    public static Uri uri;

    DatabaseReference adminDB;
    StorageReference storageReference;
    String str, pushkey;
    Utils utils;
    ArrayList<AboutData> arrayList;
    RecyclerView recyclerView;
    AboutAdapter aboutAdapter;
    AboutData aboutData;
    MaterialButton addBtn;
    FBUtils fbUtils;
    EditText editText;
    HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        recyclerView = findViewById(R.id.recycler_view);
        addBtn = findViewById(R.id.add_btn);
        editText = findViewById(R.id.edit_text);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fbUtils = new FBUtils();
        adminDB = fbUtils.getAdminDatabase();
        storageReference = fbUtils.getAdminStorage();
        utils = new Utils(this);
        arrayList = new ArrayList<>();
        map = new HashMap<>();
        utils.showProgress("", "Loading");
        adminDB.child("About_Us")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            arrayList.add(dataSnapshot.getValue(AboutData.class));
                        }
                        aboutAdapter = new AboutAdapter(AboutActivity.this, arrayList);
                        recyclerView.setAdapter(aboutAdapter);
                        utils.dismissProgress();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        utils.dismissProgress();
                    }
                });
        adminDB.child("Header")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        map.clear();
                        for( DataSnapshot ds : snapshot.getChildren() ){
                            map = (HashMap<String, String>) ds.getValue();
                        }
                        editText.setText(map.get("text"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                map.put("text", s.toString());
                adminDB.child("Header")
                        .child(map.get("push_key"))
                        .setValue(map)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: "+e.getMessage());
                            }
                        });

            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AboutActivity.this, EditActivity.class));
            }
        });


    }


}


/*str = "We are the Pioneers in the Professional Entrance Exams.\nWe help to turn our Students dreams into reality.\n" +
                "Our Educators are the foundations for our success stories of students.\n" +
                "An overall experience of 20+ years experience in producing Doctors & IITians by our Expertise faculties..\n\n" +
                "CORE TEAM:\n" +
                "This is our Core team of Academicians who will do everything to achieve what a Student have\n" +
                "dreamt for. We take this opportunity to present to all of you Our Dream Team of Faculties…..\n\n" +
                "1. ACADEMIC MENTOR\n" +
                "Mrs.Ayesha Jaweed\n" +
                "Correspondent\n" +
                "Green Valley Schools, Matric & CBSE\n" +
                "Pernambut.\n" +
                "She is an inspiration for all of us. FCSA is a Brain child of her. Always worked for the upliment of\n" +
                "all sectors of Students in Education. Her Vision is to share her knowledge and wisdom with\n" +
                "students for their success.\n\n" +
                "2. Dr. Rajalakshmi\n" +
                "Professor in ZOOLOGY\n" +
                "She herself is an inspiration for all our students, with 25+ years of Experience in given amazing\n" +
                "results for Class 12. She has worked in various institutions and have many achievements to her\n" +
                "credit. She has also made her students to achieve incredible heights in their careers .\n\n" +
                "3. ACADEMIC INCHARGE-NEET\n" +
                "BIOLOGY\n" +
                "Mr.R.Khadir Khan\n" +
                "He is an experience Academician for over 18+ years by transforming many NEET aspirants into\n" +
                "Doctors. He has worked with many reputed institutions in his career. Presently he is an DSE with\n" +
                "UNACADEMY as well. He is also authoring an NEET Biology book. His vision is to make each and\n" +
                "every student of 12" +
                "th to crack Competitive exams with impeccable scores.\n\n" +
                "4. Asst Professor of BOTANY\n" +
                "Mr.Kota Vijayakumar\n" +
                "He has a vast experience of 12+ years in Botany. He has worked in Top Institutions across India. He is from Bangalore. His vision is to make more students to crack NEET with exceptional result.\n\n" +
                "5. Asst professor of ZOOLOGY\n" +
                "Mr.B.V.N.V.Prasad\n" +
                "He has 15+ years of experience as Educator in Zoology. Have worked with various Institutions. One of the Best subject experts in Zoology. He has produced many Doctors in his career.\n\n" +
                "6. ZOOLOGY Academician\n" +
                "Ms.Ramya Venkatesan\n" +
                "She has 6+years as an Educator with a Zeal to learn. Result oriented educator, wants to transform\n" +
                "the lives of students through Education.\n\n" +
                "7. Asst Professor of PHYSICS\n" +
                "Mr. Shaikh Mukhter\n" +
                "He is an Amazing personality with 13+ years of experience in producing Doctors & IITians in his\n" +
                "career. Excellent academic track record and experience in bringing out the hidden topper in every\n" +
                "student. He is from Kadapa.\n\n" +
                "8. IIT-JEE Academician-PHYSICS\n" +
                "Mr. Bhavik Barot\n" +
                "At very young age he has more produced many IITians & NITians. He is also very good motivator. He has worked in reputed institutes.\n\n" +
                "9. Asst Professor of CHEMISTRY\n" +
                "Mrs.Lakshmi\n" +
                "Having 15+ years of experience in producing exceptional results. She has worked in reputed\n" +
                "Institutions as well. Her method of implementing Concepts is amazing and treat to watch.\n\n" +
                "10. Asst Professor of CHEMISTRY\n" +
                "Mr.Savanapally Bharat\n" +
                "He is excellent Academician and role model for students from his amazing teaching skills. He has 5+ years of experience working with Sri Chaitanya groups & Narayana groups. Result oriented Academician concentrating even on chore skill development of students.\n\n" +
                "11. NEET Academic Co-ordinator\n" +
                "CHEMISTRY\n" +
                "Ms. Lavanya\n" +
                "Talented Academician with lot of passion towards teaching and making students to achieve\n" +
                "impossible with her Innovave Approach in teaching using advance Technology.\n\n" +
                "12. Asst Professor of CHEMISTRY\n" +
                "Mr.S.Suresh Babu\n" +
                "His passion towards teaching is unparalled with 20+ years of experience. Transformed many\n" +
                "students into IITians/Doctors.\n\n" +
                "13. Asst professor of MATHS\n" +
                "Mrs.Elavarasi.D\n" +
                "Stupendous achievements as Educator with a career spanning over 15+ years. Transformed the\n" +
                "life of ‘n’ number of students to perform excellently in their Competitive exams" +
                ".";*/