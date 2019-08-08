package com.project.explore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class InsertActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 42;
    public ExploreDeals deals = new ExploreDeals();
    ImageView img;
    private EditText title;
    private EditText price;
    private EditText description;
    private Button btn;
    private FirebaseDatabase mdatabase;
    private DatabaseReference mreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);


        mdatabase = FireBaseUtil.firebaseDatabase;
        mreference = FireBaseUtil.databaseReference;


        title = findViewById(R.id.deal_title);
        price = findViewById(R.id.deal_price);
        description = findViewById(R.id.deal_description);
        btn = findViewById(R.id.deal_button);
        img = findViewById(R.id.deal_image);

        try {
            showImage(deals.getImageUrl());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //upload image from phone
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("image/jpeg");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        Intent intent = getIntent();
        ExploreDeals travelDeal = intent.getParcelableExtra("Deal");
        if (travelDeal == null) {
            travelDeal = new ExploreDeals();
        }


        deals = travelDeal;
        title.setText(deals.getTitle());
        description.setText(deals.getDescription());
        price.setText(deals.getPrice());
        try {
            showImage(deals.getImageUrl());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //store image uri in firebase storage
            Uri imageUri = data.getData();
            StorageReference ref = FireBaseUtil.storageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            deals.setImageUrl(url);
                            showImage(url);
                        }
                    });
                }
            });

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FireBaseUtil.isAdmin) {
            menu.findItem(R.id.save_deal).setVisible(true);
            menu.findItem(R.id.delete_deal).setVisible(true);
            editEnable(true);
        } else {
            menu.findItem(R.id.save_deal).setVisible(false);
            menu.findItem(R.id.delete_deal).setVisible(false);
            editEnable(false);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_deal:
                save();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clear();
                backToList();
                return true;
            case R.id.delete_deal:
                delete();
                Toast.makeText(this, "Deal happily deleted", Toast.LENGTH_LONG).show();
                backToList();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void save() {
        deals.setTitle(title.getText().toString());
        deals.setPrice(price.getText().toString());
        deals.setDescription(description.getText().toString());

        if (deals.getId() == null) {
            mreference.push().setValue(deals);
        } else {
            mreference.child(deals.getId()).setValue(deals);
        }
    }

    private void editEnable(boolean isEnabled) {
        price.setEnabled(isEnabled);
        description.setEnabled(isEnabled);
        title.setEnabled(isEnabled);
        btn.setEnabled(isEnabled);
    }

    public void delete() {
        if (deals.getId() == null) {
            Toast.makeText(this, "You had better save deal before deleting", Toast.LENGTH_LONG).show();
        } else {
            mreference.child(deals.getId()).removeValue();
        }
    }

    public void clear() {
        title.setText("");
        price.setText("");
        description.setText("");
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void showImage(String url) {
        if (url != null & url.isEmpty() == false) {
            //get width of screen occupied
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Log.d("show", "showed");
            Picasso.with(this)
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(img);
        }
    }
}
