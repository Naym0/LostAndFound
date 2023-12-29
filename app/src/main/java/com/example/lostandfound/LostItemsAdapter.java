package com.example.lostandfound;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class LostItemsAdapter extends FirestoreRecyclerAdapter<LostItems_data, LostItemsAdapter.MyViewHolder>  {
    FirebaseStorage storageReference = FirebaseStorage.getInstance();
    Bitmap bitmap;
    String path = "images/";

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
         * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public LostItemsAdapter(FirestoreRecyclerOptions<LostItems_data> options) {
        super(options);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        view = mInflater.inflate(R.layout.cardview_lostfounditem,parent,false);
        return new LostItemsAdapter.MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, int position, LostItems_data model) {
        holder.item.setText(model.getItem());
        holder.description.setText(model.getDescription());
        holder.category.setText(model.getCategory());
        holder.dateFound.setText(model.getDateFound());
        holder.location.setText(model.getLocation());
        storageReference.getReference(path+model.getImage()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                holder.image.setImageBitmap(bitmap);
                holder.image.setVisibility(View.VISIBLE);
            }
        });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item, description, category, dateFound, location;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.itemNameA);
            description = itemView.findViewById(R.id.descA);
            category = itemView.findViewById(R.id.categoryA);
            dateFound = itemView.findViewById(R.id.dateA);
            location = itemView.findViewById(R.id.collectionA);
            image = itemView.findViewById(R.id.img_obj);
        }
    }
}
