package com.example.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Path extends AppCompatActivity {
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);
        final Activity activity = this;
        activity.setTitle("Lost and Found");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dot_menu, menu);
        return true;
    }

    public void Proceed(View v){
        if (v.getId() == R.id.proceedAdd){
            Intent intent = new Intent(this, AddLostItem.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.proceedView){
            Intent intent = new Intent(this, LostItems.class);
            startActivity(intent);
        }
    }
}
