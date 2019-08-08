package com.project.explore;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FireBaseUtil {
    private static final int RC_SIGN_IN = 123;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FireBaseUtil fireBaseUtil;
    public static FirebaseStorage storage;
    public static StorageReference storageReference;
    public static FirebaseAuth mFireBaseAuth;
    public static boolean isAdmin;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<ExploreDeals> mDeals;
    private static ListActivity caller;

    public FireBaseUtil() {
    }

    public static void firebase(String ref, final ListActivity callerbackActivity) {
        if (fireBaseUtil == null) {
            fireBaseUtil = new FireBaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            caller = callerbackActivity;

            //connect to storage


            mFireBaseAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {

                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    if (firebaseAuth.getCurrentUser() == null) {

                        FireBaseUtil.setRcSignIn();
                    } else {
                        String userId = firebaseAuth.getUid();
                        checkAdmin(userId);

                        Toast.makeText(callerbackActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                    }


                }
            };

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference().child("deals_pictures");
        }
        mDeals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String userId) {
        FireBaseUtil.isAdmin = false;
        DatabaseReference ref = firebaseDatabase.getReference().child("administrators").child(userId);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FireBaseUtil.isAdmin = true;
                //call the show menu here
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener() {
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void removeListener() {
        mFireBaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private static void setRcSignIn() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        //you can't start an activity from outside an activity

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

}
