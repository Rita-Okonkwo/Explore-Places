package com.project.explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListActivity extends AppCompatActivity {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


    }

    @Override
    protected void onResume() {
        super.onResume();
        FireBaseUtil.firebase("exploredeals", this);
        mDatabase = FireBaseUtil.firebaseDatabase;
        mRef = FireBaseUtil.databaseReference;

        RecyclerView recyclerView = findViewById(R.id.list_recycler);
        final ExploreAdapter adapter = new ExploreAdapter();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FireBaseUtil.attachListener();


    }

    @Override
    protected void onPause() {
        super.onPause();
        FireBaseUtil.removeListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.new_menu_item);
        if (FireBaseUtil.isAdmin) {
            insertMenu.setVisible(true);
        } else {
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_menu_item:
                Intent intent = new Intent(this, InsertActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                FireBaseUtil.attachListener();
                            }
                        });
                FireBaseUtil.removeListener();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    public void showMenu() {
        //the invalidate options menu is used to tell android that the contents of a menu has changed
        //hence the menu should be withdrawn
        invalidateOptionsMenu();
    }
}

