package com.icebreakers.nexxus.persistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.icebreakers.nexxus.models.Profile;

/**
 * Created by amodi on 4/6/17.
 */

public class Database {

    private static Database instance;
    public DatabaseReference databaseReference;

    public static final String PROFILE_TABLE = "Profiles";

    private Database() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public static Database instance() {
        if (instance == null) {
            Database database = new Database();
            instance = database;
        }
        return instance;
    }

    public void insertProfileValue(Profile profile) {
        databaseReference.child(PROFILE_TABLE).child(profile.id).setValue(profile);
    }
}
