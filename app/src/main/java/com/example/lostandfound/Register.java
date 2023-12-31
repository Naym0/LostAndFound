package com.example.lostandfound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class Register extends AppCompatActivity {
    EditText rname, remail, rpass, rcpass, resetemail, resetpass;
    Button register, submit;
    TextView resendemail;
    ProgressBar progressBar;
    FirebaseAuth mfirebaseAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;
    RadioGroup radiogroup;
    private static final String TAG = "Register Activity!!!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mfirebaseAuth= FirebaseAuth.getInstance();
        register = findViewById(R.id.btnregister);
        rname = findViewById(R.id.nameText);
        remail = findViewById(R.id.emailText);
        rpass = findViewById(R.id.passText);
        rcpass = findViewById(R.id.confirmText);
        resendemail = findViewById(R.id.textView1);
        progressBar = findViewById(R.id.pb_register);

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String name = rname.getText().toString();
                radiogroup = findViewById(R.id.radio);
                final String email = remail.getText().toString().trim();
                String pass = rpass.getText().toString().trim();
                String cpass = rcpass.getText().toString().trim();

                //check that no field is left empty
                if(name.isEmpty()){
                    rname.setError("Please enter your name");
                    rname.requestFocus();
                }
                else if(pass.isEmpty()){
                    rpass.setError("Please enter your password");
                    rpass.requestFocus();
                }
                else if(email.isEmpty()){
                    remail.setError("Please enter a valid email address");
                    remail.requestFocus();
                }
                else if(cpass.isEmpty()){
                    rpass.setError("Please confirm your password");
                    rpass.requestFocus();
                }
                else if(!(cpass.equals(pass))){
                    rcpass.setError("Please confirm that both passwords match");
                    rcpass.requestFocus();
                }
                else if(!isEmailValid(email)){
                    remail.setError("Please use a valid email address");
                    remail.requestFocus();
                }
                else if(!(email.isEmpty() && pass.isEmpty())){
                    //save user data into Firebase Auth
                    mfirebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){

                                SendVerificationEmail();

                                userID = mfirebaseAuth.getCurrentUser().getUid();
                                //save user data into Firestore using corresponding Firebase Auth user ID
                                DocumentReference documentReference = db.collection("User").document(userID);
                                Map<String, Object> User = new HashMap<>();
                                User.put("name", name);
                                User.put("email", email);
                                User.put("account", "user");
                                if (radiogroup.getCheckedRadioButtonId() == R.id.radioFemale){
                                    User.put("gender", "Female");
                                } else {
                                    User.put("gender", "Male");
                                }

                                documentReference.set(User).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "..................................ONSUCCESS: Data Stored successfully for user " +userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d(TAG, "..................................ONFAILURE: " + e.toString());
                                    }
                                });
                            }
                            else{
                                Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Register.this, "An Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //resend verification email
        resendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                View view = getLayoutInflater().inflate(R.layout.verification_dialog, null);
                resetemail = view.findViewById(R.id.veriemail);
                resetpass = view.findViewById(R.id.veripass);
                submit = view.findViewById(R.id.veributton);
                builder.setTitle("Resend Verification email");
                builder.setMessage("Please fill in your email address to receive another verification email.");
                builder.setView(view);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email1 = resetemail.getText().toString().trim();
                        String pass1 = resetpass.getText().toString();

                        //check that no field is empty
                        if ((email1.isEmpty()) || (pass1.isEmpty())) {
                            Toast.makeText(Register.this, "Both fields must be filled in!", Toast.LENGTH_LONG).show();
                        } else {
                            ResendEmail(email1, pass1);
                        }
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    //function to check only valid email addresses are entered
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // FIREBASE SETUP CODE!

    public void SendVerificationEmail(){
        mfirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                FirebaseUser mFirebaseUser = mfirebaseAuth.getCurrentUser();
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Registration successful. Please check your email to verify account", Toast.LENGTH_LONG).show();
                    Log.d(TAG, ".................................SENDVERIFICATIONEMAIL: Email sent to: " + mFirebaseUser.getEmail());
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(Register.this, "Error, could not send verification email", Toast.LENGTH_LONG).show();
                    Log.d(TAG, ".................................SENDVERIFICATIONEMAIL: Email NOT sent: " + mFirebaseUser.getEmail());
                }
            }
        });
    }

    public void ResendEmail(String email1, String pass1) {
        AuthCredential credential = EmailAuthProvider.getCredential(email1, pass1);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "..............................onComplete: Reauthentication Success");
                            mfirebaseAuth.getCurrentUser().sendEmailVerification();
                            Toast.makeText(Register.this, "Email resent. Please check your email to verify account", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "..............................onComplete: Email sent!");
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Register.this, MainActivity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Register.this, "Invalid credenials!\nReset your password and try again.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "........................onFailure: Invalid credentials");
            }
        });
    }
}
