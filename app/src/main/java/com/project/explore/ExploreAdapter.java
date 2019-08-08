package com.project.explore;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreHolder> {
    ImageView imageView;
    ArrayList<ExploreDeals> mDeals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    public ExploreAdapter() {
        //FireBaseUtil.openfbreference("traveldeals");
        mFirebaseDatabase = FireBaseUtil.firebaseDatabase;
        mDatabaseReference = FireBaseUtil.databaseReference;
        mDeals = FireBaseUtil.mDeals;


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ExploreDeals travelDeal = dataSnapshot.getValue(ExploreDeals.class);
                Log.d("Deal:", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                mDeals.add(travelDeal);
                notifyItemInserted(mDeals.size() - 1);

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
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    @NonNull
    @Override
    public ExploreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_holder, parent, false);
        return new ExploreHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreHolder holder, int position) {
        ExploreDeals tv = mDeals.get(position);
        holder.bind(tv);
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }


    public class ExploreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvPrice;
        TextView tvDes;


        public ExploreHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.list_title);
            tvDes = itemView.findViewById(R.id.list_des);
            tvPrice = itemView.findViewById(R.id.list_price);
            imageView = itemView.findViewById(R.id.liat_image);
            itemView.setOnClickListener(this);
        }

        public void bind(ExploreDeals tv) {
            tvTitle.setText(tv.getTitle());
            tvDes.setText(tv.getDescription());
            tvPrice.setText(tv.getPrice());
            showImage(tv.getImageUrl());

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ExploreDeals selectedDeal = mDeals.get(position);
            Intent intent = new Intent(v.getContext(), InsertActivity.class);
            intent.putExtra("Deal", selectedDeal);
            v.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && url.isEmpty() == false) {
                Picasso.with(imageView.getContext())
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }

}
