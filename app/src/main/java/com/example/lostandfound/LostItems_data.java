package com.example.lostandfound;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class LostItems_data {
    String item,description, category, dateFound, location, image;
    Uri val;
    Bitmap bitmap;
    FirebaseStorage storageReference = FirebaseStorage.getInstance();

    public LostItems_data(){
        //Empty constructor needed for Firebase to create objects from db documents
    }

    public LostItems_data(String name, String desc, String category, String date, String location, String image) {
        this.item = name;
        this.description = desc;
        this.category = category;
        this.dateFound = date;
        this.location = location;
        this.image = image;
    }

    public String getItem() {
        return item;
    }

    public String getDesc() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return dateFound;
    }

    public String getLocation() { return location; }

    public Bitmap getImage() {
//        storageReference.getReference("images/"+image).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(Task<Uri> task) {
//                val = task.getResult();
//                Log.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Val", ".................................. "+val);
//            }
//        });

        storageReference.getReference("images/"+image).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            }
        });

        return bitmap;
    }
}
