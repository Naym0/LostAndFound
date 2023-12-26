package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Animation logo;
    ImageView image;
    FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "Main Activity!!!!!";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        image = findViewById(R.id.imageView2);
        image.setAnimation(logo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null && currentUser.isEmailVerified()){
            if(mfirebaseAuth.getCurrentUser().getEmail() == "Admin@lostandfound.com")
                startActivity(new Intent(MainActivity.this, Path.class));
            else
                startActivity(new Intent(MainActivity.this, LostItems.class));

            Log.d(TAG, "........................................OnAuthStateChanged: SIGNED IN: " + currentUser.getEmail());
            finish();
        }
        else{
            Log.d(TAG, "........................................OnAuthStateChanged: NOPE: ");
        }
    }

    public void onClick(View v){
        if (v.getId() == R.id.btnregistermain){
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.btnloginmain){
            startActivity(new Intent(this, Login.class));
        }
    }
}