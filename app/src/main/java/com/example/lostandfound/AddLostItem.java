package com.example.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddLostItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    EditText rname, rdesc, rdate, rlocation;
    Spinner rcategory;
    Button submit, takePic;
    ImageView imageView;
    Bitmap image;
    int CAMERA_PICTURE = 1;
    FirebaseAuth mfirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "AddLostItem Activity!!!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlostitem);

        mfirebaseAuth= FirebaseAuth.getInstance();
        submit = findViewById(R.id.btnadd);
        rname = findViewById(R.id.lostItemNameText);
        rdesc = findViewById(R.id.descriptionText);
        rdate = findViewById(R.id.dateFoundText);
        rlocation = findViewById(R.id.collectionText);
        imageView = findViewById(R.id.imagePreview);

        setSpinner();

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Bitmap picture = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(picture);
                            image = picture;
                        }
                    }
                });

        takePic = findViewById(R.id.takePic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                String itemName = rname.getText().toString();
                String desc = rdesc.getText().toString().trim();
                String category = String.valueOf(rcategory.getSelectedItem()).trim();
                String date = rdate.getText().toString().trim();
                String location = rlocation.getText().toString().trim();

                if(itemName.isEmpty()){
                    rname.setError("Please enter item name");
                    rname.requestFocus();
                }
                else if(desc.isEmpty()){
                    rdesc.setError("Please enter item description");
                    rdesc.requestFocus();
                }
                else if(category.isEmpty()){
                    Toast.makeText(AddLostItem.this, "Please select the item category", Toast.LENGTH_SHORT).show();
                    rcategory.requestFocus();
                }
                else if(date.isEmpty()){
                    rdate.setError("Please enter the date the item was found");
                    rdate.requestFocus();
                }
                else if(location.isEmpty()){
                    rlocation.setError("Please enter the collection office");
                    rlocation.requestFocus();
                }
                else if(!(itemName.isEmpty() && desc.isEmpty() && category.isEmpty() && date.isEmpty() && location.isEmpty())) {

                    CollectionReference collectionReference = db.collection("Items");
                    Map<String, Object> Item = new HashMap<>();
                    Item.put("item", itemName);
                    Item.put("description", desc);
                    Item.put("category", category);
                    Item.put("dateFound", date);
                    Item.put("location", location);
                    Item.put("image", image);

                    collectionReference.add(Item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(AddLostItem.this, "Item saved Successfully", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            //TODO: Refresh the form
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.d(TAG, "..................................ONFAILURE: " + e.toString());
                        }
                    });
                }
                else{
                    Toast.makeText(AddLostItem.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //DOESNT NEED IMPLEMENTATION
    }

    public void setSpinner(){
        // Spinner click listener
        rcategory = findViewById(R.id.spinner1);
        rcategory.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Electronic");
        categories.add("Bags");
        categories.add("Wallet/Purse");
        categories.add("Jewellery");
        categories.add("Identification documents");
        categories.add("Bank cards");
        categories.add("Clothing");
        categories.add("Shoes");
        categories.add("Watches");
        categories.add("Bikes");
        categories.add("Keys");
        categories.add("Glasses");
        categories.add("Bottles");
        categories.add("Books");
        categories.add("Miscellaneous");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        rcategory.setAdapter(dataAdapter);
    }

}
