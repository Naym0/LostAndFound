package com.example.lostandfound;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddLostItem extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    EditText rname, rdesc, rdate, rlocation;
    Spinner spinner;
    Button submit, takePic;
    ImageView imageView;
    byte[] imageByte;
    Uri stored;
    FirebaseAuth mfirebaseAuth;
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
        rlocation = findViewById(R.id.collectionText);
        imageView = findViewById(R.id.imagePreview);

        setSpinner();
        spinner.setOnItemSelectedListener(this);

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap picture = (Bitmap) data.getExtras().get("data");
                            imageView.setImageBitmap(picture);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraLauncher.launch(cameraIntent);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, stored);
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
                String category = String.valueOf(spinner.getSelectedItem()).trim();
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
                    spinner.requestFocus();
                }
                else if(date.isEmpty()){
                    rdate.setError("Please enter the date the item was found");
                    rdate.requestFocus();
                }
                else if(location.isEmpty()){
                    rlocation.setError("Please enter the collection office");
                    rlocation.requestFocus();
                }
                else if(imageByte == null){
                    Toast.makeText(AddLostItem.this, "Please take a picture for upload", Toast.LENGTH_SHORT).show();
                }
                else if(!(itemName.isEmpty() && desc.isEmpty() && category.isEmpty() && date.isEmpty() && location.isEmpty())) {

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

    public void setSpinner(){
        // Spinner click listener
        spinner = findViewById(R.id.spinner1);

        // Creating adapter for spinner using resource file
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    private void uploadToFirebase(byte[] Byte, Map Item){
        UUID id = UUID.randomUUID();
        final StorageReference imageRef = storageReference.child(("images/"+id));

        imageRef.putBytes(Byte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Item.put("image", id.toString());
                        collectionReference.add(Item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(AddLostItem.this, "Item saved Successfully", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                                finish();
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
    private String getFileExtension(Uri fileUri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(fileUri));
    }
}
