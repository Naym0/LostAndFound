package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class LostItems extends AppCompatActivity {
    private static final String TAG = "Lost and Found Items!!!!!";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ref = db.collection("Items");
    LostItemsAdapter adapter;
    FirebaseAuth user = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfounditems);
        setRecyclerView();
    }

    private void setRecyclerView() {
        Query query = ref;

        FirestoreRecyclerOptions<LostItems_data> options = new FirestoreRecyclerOptions
                .Builder<LostItems_data>()
                .setQuery(query, LostItems_data.class)
                .build();

        adapter = new LostItemsAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.rview_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dot_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.profile){
            Intent intent = new Intent(this, Profile.class);
            startActivity(intent);
        }
        else{
            user.signOut();
            Toast.makeText(LostItems.this,"You have successfully logged out",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
