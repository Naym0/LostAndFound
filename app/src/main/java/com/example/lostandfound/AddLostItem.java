package com.example.lostandfound;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddLostItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    EditText rname, rdesc, rdate;
    Spinner spinner1, spinner2;
    Button submit, takePic, pickDate;
    ImageView imageView;
    byte[] imageByte;
    Uri stored;
    FirebaseAuth mfirebaseAuth;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Items");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
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
        imageView = findViewById(R.id.imagePreview);
        progressBar = findViewById(R.id.pb_add);

        setCategorySpinner();;
        spinner1.setOnItemSelectedListener(this);

        setLocationSpinner();;
        spinner2.setOnItemSelectedListener(this);

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //get result from camera instance
                            Intent data = result.getData();
                            //convert to Bitmap for display
                            Bitmap picture = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(picture);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            //compress it for save in Firebase
                            picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            imageByte = baos.toByteArray();
                        }
                        else{
                            Toast.makeText(AddLostItem.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        takePic = findViewById(R.id.takePic);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if camera permission is granted, it yes, launch camera
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, stored);
                }
                else{ //request permission
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        pickDate = findViewById(R.id.pickDate);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get calendar instance
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // create a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddLostItem.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                rdate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // pass year, month and day for selected date in our date picker.
                        year, month, day);
                // display date picker dialog.
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                progressBar.setVisibility(View.VISIBLE);
                String itemName = rname.getText().toString();
                String desc = rdesc.getText().toString().trim();
                String category = String.valueOf(spinner1.getSelectedItem()).trim();
                String date = rdate.getText().toString().trim();
                String location = String.valueOf(spinner2.getSelectedItem()).trim();

                //check that no field is left empty
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
                    spinner1.requestFocus();
                }
                else if(date.isEmpty()){
                    rdate.setError("Please enter the date the item was found");
                    rdate.requestFocus();
                }
                else if(location.isEmpty()){
                    Toast.makeText(AddLostItem.this, "Please select the collection office", Toast.LENGTH_SHORT).show();
                    spinner2.requestFocus();
                }
                else if(imageByte == null){
                    Toast.makeText(AddLostItem.this, "Please take a picture for upload", Toast.LENGTH_SHORT).show();
                }
                else if(!(itemName.isEmpty() && desc.isEmpty() && category.isEmpty() && date.isEmpty() && location.isEmpty())) {

                    //create hashmap of the data to be saved
                    Map<String, Object> Item = new HashMap<>();
                    Item.put("item", itemName);
                    Item.put("description", desc);
                    Item.put("category", category);
                    Item.put("dateFound", date);
                    Item.put("location", location);

                    Log.d(TAG, "..................................DATA TO BE SAVED " +Item);
                    uploadToFirebase(imageByte, Item);
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
        //DOESN'T NEED IMPLEMENTATION
    }

    public void setCategorySpinner(){
        // Spinner click listener
        spinner1 = findViewById(R.id.spinner1);

        // Creating adapter for spinner using resource file
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner1.setAdapter(dataAdapter);
    }

    public void setLocationSpinner(){
        // Spinner click listener
        spinner2 = findViewById(R.id.spinner2);

        // Creating adapter for spinner using resource file
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this, R.array.locations_array, android.R.layout.simple_spinner_item);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner2.setAdapter(dataAdapter);
    }

    private void uploadToFirebase(byte[] Byte, Map Item){
        //create a random ID to save image in Firebase Storage
        UUID id = UUID.randomUUID();
        final StorageReference imageRef = storageReference.child(("images/"+id));

        //save images
        imageRef.putBytes(Byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Item.put("image", id.toString());
                        //save image ID into corresponding Firestore record of lost item
                        collectionReference.add(Item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddLostItem.this, "Item saved Successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                finish();
                                startActivity(new Intent(AddLostItem.this, AddLostItem.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.d(TAG, "..................................ONFAILURE: " + e.toString());
                            }
                        });
                    }
                });
                Log.d(TAG, "Image saved to Firebase Storage");
            }
        });
    }

}
