package com.example.scatendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.security.auth.Subject;

public class main2 extends AppCompatActivity {

    private TextView AddressText;
    private Button LocationButton;
    private Button logout;
    private LocationRequest locationRequest;
    public static double longi,lati;
    private FloatingActionButton fab;
    private static final int CAMERA_PERMISSION_CODE = 100;
    public static String scaninfo;
    public TextView mscandata;
    public static Double laticom;
    public static Double longicom;
    public static String timecomp;
    public static String datecomp;


    private EditText name;
    private  EditText rollNo;
    public static String time;
    public static String date;
    public static String subject;
    private final  String url = "http://10.0.2.2/PBL PROJECT/db_insert.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        AddressText = findViewById(R.id.location);
        LocationButton = findViewById(R.id.getlocation);
        logout = findViewById(R.id.logout);
        fab = findViewById(R.id.fab);
        mscandata = findViewById(R.id.scandata);
        name = findViewById(R.id.name);
        rollNo = findViewById(R.id.rollNo);
        date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(name == null){
//                    name.setError("Name is Required");
//                    return;
//                }
//                if(rollNo == null){
//                    rollNo.setError("Roll No is Required");
//                }

                getCurrentLocation();
                mscandata.setText(scaninfo + " " + "Date :" +date +" Time : "+ time);



                if(scaninfo == null){
                    Toast.makeText(main2.this, "Scan QR for Attendence", Toast.LENGTH_SHORT).show();


                }

                if(laticom - 0.00003 < lati && lati < laticom + 0.00003 && longicom - 0.00003 < longi &&
                longi < longicom+0.00003){


                    Toast.makeText(main2.this, "Attendence marked", Toast.LENGTH_SHORT).show();
                    scaninfo = null;

                }
                else{
                    Toast.makeText(main2.this, "Location not matched", Toast.LENGTH_SHORT).show();


                }


            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                scaninfo = null;
                lati = 0;
                longi = 0;
                finish();
                startActivity(new Intent(main2.this,MainActivity.class));


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                startActivity(new Intent(main2.this, scannerActivity.class));
            }
        });





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if (isGPSEnabled()) {

                    getCurrentLocation();

                }else {

                    turnOnGPS();
                }
            }
        }
        else if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(main2.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(main2.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentLocation();
            }
        }
    }

    private void getCurrentLocation() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(main2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(main2.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(main2.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        lati = latitude;
                                        longi = longitude;


                                        AddressText.setText("Latitude: "+ latitude + "\n" + "Longitude: "+ longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void turnOnGPS() {



        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(main2.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(main2.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(main2.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(main2.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(main2.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

//    public  void  process(View view){
//        String mname = name.getText().toString();
//        String mrollNo = rollNo.getText().toString();
//        String msubject = subject;
//        String mtime = time;
//        String mdate = date;
//        String query="?t1="+mname+"&t2="+mrollNo+"&t3="+msubject+"&t4="+mtime+"&t5="+mdate;
//
//        class dbclass extends AsyncTask<String,Void,String>{
//
//            protected void onPostExecute(String data){
//
//                name.setText("");
//                rollNo.setText("");
//                subject = subject;
//                time = time;
//                data = data;
//
//            }
//
//            @Override
//            protected String doInBackground(String... strings) {
//
//                try {
//                    URL url = new URL(strings[0]);
//                    HttpURLConnection conn =(HttpURLConnection)url.openConnection();
//                    BufferedReader br =new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                    return br.readLine();
//                }catch (Exception ex){
//                    return ex.getMessage();
//                }
//            }
//        }
//        dbclass obj= new dbclass();
//        obj.execute(url+query);
//    }

    @Override
    public void onBackPressed(){

        moveTaskToBack(true);

    }




}