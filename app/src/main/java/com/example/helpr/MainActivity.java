package com.example.helpr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import android.telephony.SmsManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private String message = "";
    private final int REQ_CODE = 100;
    TextView textView;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private FusedLocationProviderClient fusedLocationClient;
    private double lat = 0.0, lon = 0.0;
    Button sendBtn, send2Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textView = findViewById(R.id.text);
        ImageButton speak = findViewById(R.id.voiceButton);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        sendBtn = findViewById(R.id.policeButton);
        send2Btn = findViewById(R.id.fireButton);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
        send2Btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
        TextView helpr = findViewById(R.id.title);
        helpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                });
        checkLocPermission();
        checkSmsPermission();
    }
    protected void sendSMSMessage() {

        String s = " 8477678856";
        Uri uri = Uri.parse("smsto:" + s);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", message);
        startActivity(it);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("8477678856", null, "hello", null, null);
//                    String s = " 8477678856";
//                    Uri uri = Uri.parse("smsto:" + s);
//                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
//                    it.putExtra("sms_body", "Here you can set the SMS text to be sent");
//                    startActivity(it);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textView.setText(result.get(0).toString());
                    message = result.get(0).toString();
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lat = location.getLatitude();
                                        lon = location.getLongitude();
                                    }

                                }
                            });
                    message += "    latitude: " + lat + "     longitude: " + lon;
                    if (result.get(0).toString().contains("start app")) {
                        String s = result.get(0).toString().substring(9);
                        s = s.toLowerCase();
                        s = s.replaceAll("\\s", "");
                        Intent intent = null;
                        if (s.equals("pokemongo")) {
                            intent = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
                        }
                        if (intent != null) {
                            // We found the activity now start the activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            // Bring user to the market or let them choose an app?
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("market://details?id=" + s));
                            startActivity(intent);
                        }
                    }
                    if (result.get(0).toString().contains("find")) {
                        String s = result.get(0).toString().substring(5);
                        Uri location = Uri.parse("geo:0,0?q=" + s);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                        startActivity(mapIntent);
                    }
                    if (result.get(0).toString().contains("open")) {
                        String s = result.get(0).toString().substring(5);
                        s = s.toLowerCase();
                        s = s.replaceAll("\\s", "");
                        Uri webpage = Uri.parse("http://www." + s);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(webIntent);
                    }
                    if (result.get(0).toString().contains("search")) {
                        String s = result.get(0).toString().substring(6);
                        s = s.toLowerCase();
                        try {
                            String escapedQuery = URLEncoder.encode(s, "UTF-8");
                            Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        } catch(UnsupportedEncodingException w) {
                            return;
                        }
                    }
                    if (result.get(0).toString().equals("exit")) {
                        finish();
                    }
                }
                break;
            }
        }
    }
    public void checkIntPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
                checkIntPermission();
            } else {
                return;
                // Permission has already been granted

            }
        }
    }
    public void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
                checkSmsPermission();
            } else {
                return;
                // Permission has already been granted

            }
        }
    }
    public void checkLocPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);
            checkLocPermission();
        } else {
            return;
            // Permission has already been granted

        }
    }
}
