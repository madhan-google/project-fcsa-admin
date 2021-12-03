package com.codekiller.fcsaadmin.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBUtils {
    DatabaseReference queryDatabase, candidateDatabase, ebookDatabase, favoritesDatabase, candidateNameDatabase, adminDatabase, photosDatabase,
                        eventDatabase;
    StorageReference ebookStorage, adminStorage, photoStorage;
    FirebaseAuth auth;

    public FBUtils() {
        queryDatabase = FirebaseDatabase.getInstance().getReference("Query");
        candidateDatabase = FirebaseDatabase.getInstance().getReference("Candidates");
        ebookDatabase = FirebaseDatabase.getInstance().getReference("eBooks");
        favoritesDatabase = FirebaseDatabase.getInstance().getReference("Favorites");
        candidateNameDatabase = FirebaseDatabase.getInstance().getReference("Candidates_names");
        adminDatabase = FirebaseDatabase.getInstance().getReference("Admin");
        photosDatabase = FirebaseDatabase.getInstance().getReference("Photos");
        eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        auth = FirebaseAuth.getInstance();

        ebookStorage = FirebaseStorage.getInstance().getReference("eBooks");
        adminStorage = FirebaseStorage.getInstance().getReference("Teachers Photos");
        photoStorage = FirebaseStorage.getInstance().getReference("Photos");
    }

    public DatabaseReference getEventDatabase() {
        return eventDatabase;
    }

    public DatabaseReference getPhotosDatabase() {
        return photosDatabase;
    }

    public StorageReference getAdminStorage() {
        return adminStorage;
    }

    public StorageReference getPhotoStorage() {
        return photoStorage;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public DatabaseReference getQueryDatabase() {
        return queryDatabase;
    }

    public DatabaseReference getCandidateDatabase() {
        return candidateDatabase;
    }

    public DatabaseReference getEbookDatabase() {
        return ebookDatabase;
    }

    public DatabaseReference getFavoritesDatabase() {
        return favoritesDatabase;
    }

    public DatabaseReference getCandidateNameDatabase() {
        return candidateNameDatabase;
    }

    public StorageReference getEbookStorage() {
        return ebookStorage;
    }

    public DatabaseReference getAdminDatabase() {
        return adminDatabase;
    }
}
