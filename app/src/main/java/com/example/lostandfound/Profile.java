package com.example.lostandfound;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    TextView name, email, gender;
    private static final String TAG = "Profile Activity!!!!!";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DocumentReference ref = db.collection("User").document(userID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //GETTING USER DETAILS TO DISPLAY THEM
        name = findViewById(R.id.profile_name1);
        email = findViewById(R.id.profile_email);
        gender = findViewById(R.id.profile_gender);

        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "............................DocumentSnapshot data: \n" + document.getData());
                        name.setText(document.getString("name"));
                        email.setText(document.getString("email"));
                        gender.setText(document.getString("gender"));
                    } else {
                        Log.d(TAG, "............................No such document");
                    }
                }
                else{
                    Log.d(TAG, "..........................ERROR: " +task.getException().getMessage());
                }
            }
        });

    }
}
