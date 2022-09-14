package com.example.scatendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button loginbtn;
    EditText U_id;
    EditText pwd;
    String muserid, mpass;

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginbtn = findViewById(R.id.loginbtn);
        U_id = findViewById(R.id.userid);
        pwd = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, main2.class));
            finish();
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                muserid = U_id.getText().toString();
                mpass = pwd.getText().toString();

                if (TextUtils.isEmpty(muserid)) {
                    U_id.setError("User ID is Required");
                    return;
                }
                if (TextUtils.isEmpty(mpass)) {
                    pwd.setError("Password ID is Required");
                    return;
                }


                mAuth.signInWithEmailAndPassword(muserid, mpass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Authentication Sucess.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, main2.class));

                                } else {
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }
}