package com.example.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

    //navigation options in threee dot menu in taskbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.profile){
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        }
        else{
            user.signOut();
            Toast.makeText(Path.this,"You have successfully logged out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //navigation to either add a new item or view the present ones
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
