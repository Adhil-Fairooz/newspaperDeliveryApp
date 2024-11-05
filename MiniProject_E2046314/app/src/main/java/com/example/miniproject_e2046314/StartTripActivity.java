package com.example.miniproject_e2046314;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.annotation.SuppressLint;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StartTripActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String TAG = StartTripActivity.class.getSimpleName();
    private GoogleMap googleMap;
    private UiSettings uiSettings;
    private String subscriber_id;
    private String paperId;
    private String deliveryStatus;
    // for offline pupose next button //
    private List<String> subArrtId = new ArrayList<>();
    // Update Form Stuff
    private TextView JtvSubsName, JtvAddress, JtvPlanID, Jtvtype, JtvNewspaper, JtvExpire;
    private EditText JetRenualStatus, JetDelNotes;

    private RadioGroup radioGroup;
    private RadioButton radioDelivered, radioNotDelivered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_trip);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
        //form stuff
        JtvSubsName = (TextView) findViewById(R.id.tvSubscriberName);
        JtvAddress = (TextView) findViewById(R.id.tvaddress);
        JtvPlanID = (TextView) findViewById(R.id.tvPlanID);
        Jtvtype = (TextView) findViewById(R.id.tvType);
        JtvNewspaper = (TextView) findViewById(R.id.tvNewspapers);
        JtvExpire = (TextView) findViewById(R.id.tvExpireDate);

        JetRenualStatus = (EditText) findViewById(R.id.etRenwalSatus);
        radioDelivered = (RadioButton) findViewById(R.id.radioDelivered);
        radioNotDelivered = (RadioButton) findViewById(R.id.radioNotDelivered);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioDelivered.getId()) {
                    deliveryStatus = "Delivered";
                } else if (checkedId == radioNotDelivered.getId()) {
                    deliveryStatus = "Not Delivered";
                } else {
                    deliveryStatus = "Not Delivered";
                    return;
                }
            }
        });

        JetDelNotes = (EditText) findViewById(R.id.etNote);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        // Add a marker at a specific location
        getSubscriberLocations(googleMap, ServerSignIn.UserID);


        // Move the camera to one of the locations
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.877473, 79.890100), 18f));
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        this.subscriber_id = marker.getTitle();
        getScubscriberInfoForUpdate(this.subscriber_id);
        return false;
    }

    @SuppressLint("Range")
    public void getSubscriberLocations(GoogleMap map, String userID){
        try{
            // Open the database for reading
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String query = "SELECT s."+DatabaseHelper.col_subID+",s."+DatabaseHelper.col_Fname+",s."+DatabaseHelper.col_Lname+",s."+DatabaseHelper.col_latitude+", s."+DatabaseHelper.col_longitude+ " FROM "+DatabaseHelper.tbl_subscriber+" AS s " +
                    "JOIN "+DatabaseHelper.tbl_delivery+" AS d ON s."+DatabaseHelper.col_subID+" = d."+DatabaseHelper.col_del_sub_id+" WHERE d."+DatabaseHelper.col_del_dis_id+" = '"+userID+"';";
            Cursor cursor = db.rawQuery(query, null);
            // Process the results
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve data from the cursor
                    String SubscriberID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.col_subID));
                    @SuppressLint("Range") String lat = cursor.getString(cursor.getColumnIndex(DatabaseHelper.col_latitude));
                    @SuppressLint("Range") String lon = cursor.getString(cursor.getColumnIndex(DatabaseHelper.col_longitude));
                    @SuppressLint("Range") String Fname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.col_Fname));
                    @SuppressLint("Range") String Lname = cursor.getString(cursor.getColumnIndex(DatabaseHelper.col_Lname));
                    this.subArrtId.add(SubscriberID);

                    map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))).title(SubscriberID).snippet(Fname+" "+Lname));

                    // ... process retrieved values
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();

        }catch (Exception ex){
            Log.d(TAG, "getSubscriberLocations Exception: "+ex.getMessage());
        }

    }

    private void getScubscriberInfoForUpdate(String subId){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT s."+DatabaseHelper.col_Fname+", s."+DatabaseHelper.col_Lname+",s."+DatabaseHelper.col_Address+", p."+DatabaseHelper.col_PaperID+", p."+DatabaseHelper.col_PaperName+", p."+DatabaseHelper.col_Type+", p."+DatabaseHelper.col_papers+", su."+DatabaseHelper.col_EndDate+", su."+DatabaseHelper.col_reminderStatus+" " +
                "FROM "+DatabaseHelper.tbl_subscriber+" s, "+DatabaseHelper.tbl_newspaper+" p, "+DatabaseHelper.tbl_subscription+" su " +
                "WHERE s."+DatabaseHelper.col_subID+" = su."+DatabaseHelper.col_subs_ID+" AND p."+DatabaseHelper.col_PaperID+" = su."+DatabaseHelper.col_Pap_ID+" AND s."+DatabaseHelper.col_subID+" = '"+subId+"'";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            while (cursor.moveToNext()) {
                String firstName = cursor.getString(0);
                String lastName = cursor.getString(1);
                JtvSubsName.setText(firstName+" "+lastName);
                String address = cursor.getString(2);
                JtvAddress.setText(address);
                String planId = cursor.getString(3);
                paperId = planId;
                String paperName = cursor.getString(4);
                JtvPlanID.setText(planId+" "+paperName);
                String type = cursor.getString(5);
                Jtvtype.setText(type);
                String newspapers = cursor.getString(6);
                JtvNewspaper.setText(newspapers);
                String endDate = cursor.getString(7);
                JtvExpire.setText(endDate);
                String renewalStatus = cursor.getString(8);
                JetRenualStatus.setText(renewalStatus);
            }

        }
        cursor.close();
        db.close();
        displayDeliveryinfo(subId);
    }
    private void displayDeliveryinfo(String subId){
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT "+DatabaseHelper.col_del_status+","+DatabaseHelper.col_del_note+" FROM "+DatabaseHelper.tbl_delivery+" WHERE "+DatabaseHelper.col_del_sub_id+" = '"+subId+"' AND "+DatabaseHelper.col_del_dis_id+"= '"+ServerSignIn.UserID+"'" ;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            while (cursor.moveToNext()) {
                String status = cursor.getString(0);
                if(status.equals("Delivered")){
                    radioDelivered.setChecked(true);
                }else{
                    radioNotDelivered.setChecked(true);
                }
                String note = cursor.getString(1);
                JetDelNotes.setText(note);
            }
        }
        cursor.close();
        db.close();
    }
    private void updateRenewalRemaider(String subId,String paperId){
        try{
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String renewSat = JetRenualStatus.getText().toString();
            String updateQuery = "UPDATE "+DatabaseHelper.tbl_subscription+" SET "+DatabaseHelper.col_reminderStatus+" = '"+renewSat+"' WHERE "+DatabaseHelper.col_Pap_ID+" = '"+paperId+"' AND "+DatabaseHelper.col_subs_ID+" = '"+subId+"'";
            db.execSQL(updateQuery);
            db.close();
        }catch(Exception ex){
            Log.d(TAG, "updateRenewalRemaider Error: "+ex.getMessage());
        }
    }
    private void UpdateDelivery(String subID){
        try{
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String deliveryStatus = this.deliveryStatus;
            String delNotes = JetDelNotes.getText().toString();
            String timestampofDelivery = generateTimestamp();
            String updateQuery = "UPDATE "+DatabaseHelper.tbl_delivery+" SET "+DatabaseHelper.col_del_status+" = '"+deliveryStatus+"',"+DatabaseHelper.col_del_note+" = '"+delNotes+"', "+DatabaseHelper.col_del_timestamp+" = '"+timestampofDelivery+"' WHERE "+DatabaseHelper.col_del_dis_id+" = '"+ServerSignIn.UserID+"' AND "+DatabaseHelper.col_del_sub_id+" = '"+subID+"'";
            db.execSQL(updateQuery);
            Toast.makeText(this, "Updated successfully !", Toast.LENGTH_SHORT).show();
            db.close();
        }catch(Exception ex){
            Toast.makeText(this, "Update Failed !", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "updateDelivery Error: "+ex.getMessage());
        }
    }
    private String generateTimestamp() {
        // Create a SimpleDateFormat object with desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Get the current date and time
        Date currentDate = new Date();

        // Format the date as a timestamp string
        String timestamp = dateFormat.format(currentDate);

        return timestamp;
    }
    public void updateInfo(View v){
        updateRenewalRemaider(subscriber_id,paperId);
        UpdateDelivery(subscriber_id);
    }
    private int currentPosition;
    public void nextsub(View v){

        if (currentPosition < this.subArrtId.size()) {
            String nextSubId = subArrtId.get(currentPosition);
            getScubscriberInfoForUpdate(nextSubId);
            currentPosition++;
        }
        if (currentPosition == subArrtId.size()) {
            currentPosition = 0;
        }
    }
}