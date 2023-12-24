package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LostItems extends AppCompatActivity {
    private static final String TAG = "Lost and Found Items!!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        ArrayList<String> Res = receivedIntent.getStringArrayListExtra("Results");
        if(Res.isEmpty()){
            Res.add("NO lost items to display");
        }

        setContentView(R.layout.cardview_lostfounditem);
        LostItemsAdapter myAdapter = new LostItemsAdapter(this, Res);
        RecyclerView recyclerView = findViewById(R.id.rview_items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

    }

}
