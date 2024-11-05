package com.example.miniproject_e2046314;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Home extends AppCompatActivity {
    private static final String TAG = Home.class.getSimpleName();
    private ImageButton buttonFetchData;
    private Button btnDisplay,logout;
    TextView tvUserNameDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvUserNameDisplay = (TextView) findViewById(R.id.tvUserNameDisplay);
        tvUserNameDisplay.setText(ServerSignIn.user_Name);
        buttonFetchData = findViewById(R.id.buttonFetchData);
        btnDisplay = findViewById(R.id.displayRec);
        logout = findViewById(R.id.btnlogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Home.this, "Your are Logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        btnDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRecords();
            }
        });
        buttonFetchData.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Trigger the network request and JSON parsing
                FetchDataAsyncTask task = new FetchDataAsyncTask();
                task.execute("http://"+ServerSignIn.localhost+"/XamppProjects/MiniProjectServerFiles/MiniProjectServerFiles/ServerJsonDataFile.json","http://"+ServerSignIn.localhost+"/XamppProjects/MiniProjectServerFiles/MiniProjectServerFiles/createJsonFile.php"); // Replace with the URL of your JSON file on localhost
            }
        });

    }

    private class FetchDataAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String json = "";

            try {
                GetUpdatedJSONFile(urls[1]);
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Set request method and timeouts
                connection.setRequestMethod("GET");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);

                connection.connect();

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    json = stringBuilder.toString();

                    reader.close();
                    inputStream.close();
                }

                connection.disconnect();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }
        @Override
        protected void onPostExecute(String json) {
            // Parse the JSON data
            Toast.makeText(Home.this, "JSON DATA downloaded successful", Toast.LENGTH_LONG).show();
            parseJSONData(json);
        }
    }
    private void GetUpdatedJSONFile(String url){
        try {
            URL url2 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void parseJSONData(String json) {
        try {
            // Create a JSONObject from the JSON string
            JSONObject jsonObject = new JSONObject(json);

            // Access data from the JSONObject
            JSONArray table1Array = jsonObject.getJSONArray("subscribers");
            JSONArray table2Array = jsonObject.getJSONArray("newspapers");
            JSONArray table3Array = jsonObject.getJSONArray("subscription");
            JSONArray table4Array = jsonObject.getJSONArray("delivery");
            deleteAllRecords(DatabaseHelper.tbl_subscription);
            deleteAllRecords(DatabaseHelper.tbl_newspaper);
            deleteAllRecords(DatabaseHelper.tbl_subscriber);
            deleteAllRecords(DatabaseHelper.tbl_delivery);

            // Open the database for writing
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Iterate over the arrays and retrieve values
            for (int i = 0; i < table1Array.length(); i++) {
                JSONObject table1Object = table1Array.getJSONObject(i);
                // Access specific fields from table1Object
                String field1 = table1Object.getString("SubscriberID");
                String field2 = table1Object.getString("Fname");
                String field3 = table1Object.getString("Lname");
                String field4 = table1Object.getString("Address");
                String field5 = table1Object.getString("longitude");
                String field6 = table1Object.getString("latitude");

                // Create a ContentValues object to hold the data
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.col_subID, field1);
                values.put(DatabaseHelper.col_Fname, field2);
                values.put(DatabaseHelper.col_Lname, field3);
                values.put(DatabaseHelper.col_Address, field4);
                values.put(DatabaseHelper.col_longitude, field5);
                values.put(DatabaseHelper.col_latitude, field6);

                // Insert the values into the database
                db.insert(DatabaseHelper.tbl_subscriber, null, values);
                // Do something with the retrieved data
                int count = i + 1;
                Log.d(TAG, "Subscriber records : "+count+" inserted");
            }

            for (int i = 0; i < table2Array.length(); i++) {
                JSONObject table1Object = table2Array.getJSONObject(i);
                // Access specific fields from table1Object
                String field1 = table1Object.getString(DatabaseHelper.col_PaperID);
                String field2 = table1Object.getString(DatabaseHelper.col_PaperName);
                String field3 = table1Object.getString(DatabaseHelper.col_Type);
                String field4 = table1Object.getString(DatabaseHelper.col_papers);
                String field5 = table1Object.getString(DatabaseHelper.col_Price);

                // Create a ContentValues object to hold the data
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.col_PaperID, field1);
                values.put(DatabaseHelper.col_PaperName, field2);
                values.put(DatabaseHelper.col_Type, field3);
                values.put(DatabaseHelper.col_papers, field4);
                values.put(DatabaseHelper.col_Price, field5);


                // Insert the values into the database
                db.insert(DatabaseHelper.tbl_newspaper, null, values);
                // Do something with the retrieved data
                int count = i + 1;
                Log.d(TAG, "newspaper records : "+count+" inserted");
            }

            for (int i = 0; i < table3Array.length(); i++) {
                JSONObject table1Object = table3Array.getJSONObject(i);
                // Access specific fields from table1Object
                String field1 = table1Object.getString(DatabaseHelper.col_subs_ID);
                String field2 = table1Object.getString(DatabaseHelper.col_Pap_ID);
                String field3 = table1Object.getString(DatabaseHelper.col_StartDate);
                String field4 = table1Object.getString(DatabaseHelper.col_EndDate);
                String field5 = table1Object.getString(DatabaseHelper.col_reminderStatus);

                // Create a ContentValues object to hold the data
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.col_subs_ID, field1);
                values.put(DatabaseHelper.col_Pap_ID, field2);
                values.put(DatabaseHelper.col_StartDate, field3);
                values.put(DatabaseHelper.col_EndDate, field4);
                values.put(DatabaseHelper.col_reminderStatus, field5);


                // Insert the values into the database
                db.insert(DatabaseHelper.tbl_subscription, null, values);
                // Do something with the retrieved data
                int count = i + 1;
                Log.d(TAG, "Subscription records : "+count+" inserted");

            }
            for (int i = 0; i < table4Array.length(); i++) {
                JSONObject table1Object = table4Array.getJSONObject(i);
                // Access specific fields from table1Object
                String field1 = table1Object.getString(DatabaseHelper.col_del_id);
                String field2 = table1Object.getString(DatabaseHelper.col_del_sub_id);
                String field3 = table1Object.getString(DatabaseHelper.col_del_dis_id);
                String field4 = table1Object.getString(DatabaseHelper.col_del_timestamp);
                String field5 = table1Object.getString(DatabaseHelper.col_del_status);
                String field6 = table1Object.getString(DatabaseHelper.col_del_note);

                // Create a ContentValues object to hold the data
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.col_del_id, field1);
                values.put(DatabaseHelper.col_del_sub_id, field2);
                values.put(DatabaseHelper.col_del_dis_id, field3);
                values.put(DatabaseHelper.col_del_timestamp, field4);
                values.put(DatabaseHelper.col_del_status, field5);
                values.put(DatabaseHelper.col_del_note, field6);


                // Insert the values into the database
                db.insert(DatabaseHelper.tbl_delivery, null, values);
                // Do something with the retrieved data
                int count = i + 1;
                Log.d(TAG, "delivery records : "+count+" inserted");

            }
            Toast.makeText(this, "Database Sync Completed ", Toast.LENGTH_SHORT).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception ex){
            Log.d(TAG, "parseJSONData: "+ex.getMessage());
        }
    }
    //This method is only for testing purpose To Read all data from SQLite database and display in Logcat
    private void displayRecords() {
        try{
            // Open the database for reading
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Define the columns you want to retrieve
            String[] projection = {
                    DatabaseHelper.col_subID,
                    DatabaseHelper.col_Fname,
                    DatabaseHelper.col_Lname,
                    DatabaseHelper.col_Address,
                    DatabaseHelper.col_longitude,
                    DatabaseHelper.col_latitude,
            };


            // Perform the query
            Cursor cursor = db.query(
                    DatabaseHelper.tbl_subscriber,  // The table name
                    projection,                 // The columns to retrieve
                    null,                       // The selection criteria
                    null,                       // The selection arguments
                    null,                       // The grouping criteria
                    null,                       // The filtering criteria
                    null                        // The sort order
            );


            // Iterate over the cursor to log the records
            Log.d(TAG,DatabaseHelper.tbl_subscriber);
            while (cursor.moveToNext()) {
                String field1 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_subID));
                String field2 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_Fname));
                String field3 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_Lname));
                String field4 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_Address));
                String field5 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_longitude));
                String field6 = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.col_latitude));

                Log.d(TAG, DatabaseHelper.col_subID+" : "+field1+" , "+DatabaseHelper.col_Fname+" : "+field2+" , "+DatabaseHelper.col_Lname+" : "+field3+" , "+DatabaseHelper.col_Address+" : "+field4+" , "+DatabaseHelper.col_longitude+" : "+field5+" , "+DatabaseHelper.col_latitude+" : "+field6);
            }
            String[] projection2 ={
                    DatabaseHelper.col_PaperID,
                    DatabaseHelper.col_PaperName,
                    DatabaseHelper.col_Type,
                    DatabaseHelper.col_papers,
                    DatabaseHelper.col_Price,
            };

            Cursor cursor2 = db.query(
                    DatabaseHelper.tbl_newspaper,  // The table name
                    projection2,                 // The columns to retrieve
                    null,                       // The selection criteria
                    null,                       // The selection arguments
                    null,                       // The grouping criteria
                    null,                       // The filtering criteria
                    null                        // The sort order
            );
            // Iterate over the cursor to log the records
            Log.d(TAG,DatabaseHelper.tbl_newspaper);
            while (cursor2.moveToNext()) {
                String field1 = cursor2.getString(cursor2.getColumnIndexOrThrow(DatabaseHelper.col_PaperID));
                String field2 = cursor2.getString(cursor2.getColumnIndexOrThrow(DatabaseHelper.col_PaperName));
                String field3 = cursor2.getString(cursor2.getColumnIndexOrThrow(DatabaseHelper.col_Type));
                String field4 = cursor2.getString(cursor2.getColumnIndexOrThrow(DatabaseHelper.col_papers));
                String field5 = cursor2.getString(cursor2.getColumnIndexOrThrow(DatabaseHelper.col_Price));

                Log.d(TAG, DatabaseHelper.col_PaperID+" : "+field1+" , "+DatabaseHelper.col_PaperName+" : "+field2+" , "+DatabaseHelper.col_Type+" : "+field3+" , "+DatabaseHelper.col_papers+" : "+field4+" , "+DatabaseHelper.col_Price+" : "+field5);
            }

            String[] projection3 ={
                    DatabaseHelper.col_subs_ID,
                    DatabaseHelper.col_Pap_ID,
                    DatabaseHelper.col_StartDate,
                    DatabaseHelper.col_EndDate,
                    DatabaseHelper.col_reminderStatus,
            };

            Cursor cursor3 = db.query(
                    DatabaseHelper.tbl_subscription,  // The table name
                    projection3,                 // The columns to retrieve
                    null,                       // The selection criteria
                    null,                       // The selection arguments
                    null,                       // The grouping criteria
                    null,                       // The filtering criteria
                    null                        // The sort order
            );
            // Iterate over the cursor to log the records
            Log.d(TAG,DatabaseHelper.tbl_subscription);
            while (cursor3.moveToNext()) {
                String field1 = cursor3.getString(cursor3.getColumnIndexOrThrow(DatabaseHelper.col_subs_ID));
                String field2 = cursor3.getString(cursor3.getColumnIndexOrThrow(DatabaseHelper.col_Pap_ID));
                String field3 = cursor3.getString(cursor3.getColumnIndexOrThrow(DatabaseHelper.col_StartDate));
                String field4 = cursor3.getString(cursor3.getColumnIndexOrThrow(DatabaseHelper.col_EndDate));
                String field5 = cursor3.getString(cursor3.getColumnIndexOrThrow(DatabaseHelper.col_reminderStatus));

                Log.d(TAG, DatabaseHelper.col_subs_ID+" : "+field1+" , "+DatabaseHelper.col_Pap_ID+" : "+field2+" , "+DatabaseHelper.col_StartDate+" : "+field3+" , "+DatabaseHelper.col_EndDate+" : "+field4+" , "+DatabaseHelper.col_reminderStatus+" : "+field5);
            }
            // Define the columns you want to retrieve
            String[] projection4 = {
                    DatabaseHelper.col_del_id,
                    DatabaseHelper.col_del_sub_id,
                    DatabaseHelper.col_del_dis_id,
                    DatabaseHelper.col_del_timestamp,
                    DatabaseHelper.col_del_status,
                    DatabaseHelper.col_del_note,
            };


            // Perform the query
            Cursor cursor4 = db.query(
                    DatabaseHelper.tbl_delivery,  // The table name
                    projection4,                 // The columns to retrieve
                    null,                       // The selection criteria
                    null,                       // The selection arguments
                    null,                       // The grouping criteria
                    null,                       // The filtering criteria
                    null                        // The sort order
            );


            // Iterate over the cursor to log the records
            Log.d(TAG,DatabaseHelper.tbl_delivery);
            while (cursor4.moveToNext()) {
                String field1 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_id));
                String field2 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_sub_id));
                String field3 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_dis_id));
                String field4 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_timestamp));
                String field5 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_status));
                String field6 = cursor4.getString(cursor4.getColumnIndexOrThrow(DatabaseHelper.col_del_note));

                Log.d(TAG, DatabaseHelper.col_del_id+" : "+field1+" , "+DatabaseHelper.col_del_sub_id+" : "+field2+" , "+DatabaseHelper.col_del_dis_id+" : "+field3+" , "+DatabaseHelper.col_del_timestamp+" : "+field4+" , "+DatabaseHelper.col_del_status+" : "+field5+" , "+DatabaseHelper.col_del_note+" : "+field6);
            }

            // Close the cursor and the database
            cursor4.close();
            cursor3.close();
            cursor2.close();
            cursor.close();
            db.close();
        }catch (Exception ex){
            Log.d(TAG, "ERROR Message : "+ ex.getMessage());
        }

    }
    //-------------------------------------------------------------------------------
    public void deleteAllRecords(String tableName) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete(tableName, null, null);
        Log.d("Delete", "Deleted rows: " + deletedRows);
        db.close();
    }
    public void displayTrip(View v){
        Intent i = new Intent(this,StartTripActivity.class);
        startActivity(i);
    }
    public void endTrip(View v){
        String jsonString = convertDataToJSON();
        new SendAsynTaskToServer().execute(jsonString);
    }
    private String convertDataToJSON(){
        String jsonString;
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] subscriberTBLProjection = {
                    DatabaseHelper.col_subID,
                    DatabaseHelper.col_Fname,
                    DatabaseHelper.col_Lname,
                    DatabaseHelper.col_Address,
                    DatabaseHelper.col_longitude,
                    DatabaseHelper.col_latitude,
            };
            String[] newspaperTBLProjection = {
                    DatabaseHelper.col_PaperID,
                    DatabaseHelper.col_PaperName,
                    DatabaseHelper.col_Type,
                    DatabaseHelper.col_papers,
                    DatabaseHelper.col_Price,
            };
            String[] subscriptionTBLProjection = {
                    DatabaseHelper.col_subs_ID,
                    DatabaseHelper.col_Pap_ID,
                    DatabaseHelper.col_StartDate,
                    DatabaseHelper.col_EndDate,
                    DatabaseHelper.col_reminderStatus,
            };
            String[] deliveryTBLProjection = {
                    DatabaseHelper.col_del_id,
                    DatabaseHelper.col_del_sub_id,
                    DatabaseHelper.col_del_dis_id,
                    DatabaseHelper.col_del_timestamp,
                    DatabaseHelper.col_del_status,
                    DatabaseHelper.col_del_note,
            };
            Cursor tblSubscriberCursor = db.query(
                    DatabaseHelper.tbl_subscriber,
                    subscriberTBLProjection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            Cursor tblNewspaperCursor = db.query(
                    DatabaseHelper.tbl_newspaper,
                    newspaperTBLProjection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            Cursor tblSubscpitionCursor = db.query(
                    DatabaseHelper.tbl_subscription,
                    subscriptionTBLProjection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            Cursor tbldeliveryCursor = db.query(
                    DatabaseHelper.tbl_delivery,
                    deliveryTBLProjection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            // Create a JSON object to hold the tables
            JSONObject jsonObject = new JSONObject();

            // Create a JSON array for table 1
            JSONArray subscriberArray = new JSONArray();
            while (tblSubscriberCursor.moveToNext()) {
                String field1 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_subID));
                String field2 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_Fname));
                String field3 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_Lname));
                String field4 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_Address));
                String field5 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_longitude));
                String field6 = tblSubscriberCursor.getString(tblSubscriberCursor.getColumnIndexOrThrow(DatabaseHelper.col_latitude));

                JSONObject SubscriberObject = new JSONObject();
                try {
                    SubscriberObject.put(DatabaseHelper.col_subID, field1);
                    SubscriberObject.put(DatabaseHelper.col_Fname, field2);
                    SubscriberObject.put(DatabaseHelper.col_Lname, field3);
                    SubscriberObject.put(DatabaseHelper.col_Address, field4);
                    SubscriberObject.put(DatabaseHelper.col_longitude, field5);
                    SubscriberObject.put(DatabaseHelper.col_latitude, field6);

                    subscriberArray.put(SubscriberObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONArray NewspaperArray = new JSONArray();
            while (tblNewspaperCursor.moveToNext()) {
                String field1 = tblNewspaperCursor.getString(tblNewspaperCursor.getColumnIndexOrThrow(DatabaseHelper.col_PaperID));
                String field2 = tblNewspaperCursor.getString(tblNewspaperCursor.getColumnIndexOrThrow(DatabaseHelper.col_PaperName));
                String field3 = tblNewspaperCursor.getString(tblNewspaperCursor.getColumnIndexOrThrow(DatabaseHelper.col_Type));
                String field4 = tblNewspaperCursor.getString(tblNewspaperCursor.getColumnIndexOrThrow(DatabaseHelper.col_papers));
                String field5 = tblNewspaperCursor.getString(tblNewspaperCursor.getColumnIndexOrThrow(DatabaseHelper.col_Price));

                JSONObject NewsPaperObject = new JSONObject();
                try {
                    NewsPaperObject.put(DatabaseHelper.col_PaperID, field1);
                    NewsPaperObject.put(DatabaseHelper.col_PaperName, field2);
                    NewsPaperObject.put(DatabaseHelper.col_Type, field3);
                    NewsPaperObject.put(DatabaseHelper.col_papers, field4);
                    NewsPaperObject.put(DatabaseHelper.col_Price, field5);


                    NewspaperArray.put(NewsPaperObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONArray subscriptionArray = new JSONArray();
            while (tblSubscpitionCursor.moveToNext()) {
                String field1 = tblSubscpitionCursor.getString(tblSubscpitionCursor.getColumnIndexOrThrow(DatabaseHelper.col_subs_ID));
                String field2 = tblSubscpitionCursor.getString(tblSubscpitionCursor.getColumnIndexOrThrow(DatabaseHelper.col_Pap_ID));
                String field3 = tblSubscpitionCursor.getString(tblSubscpitionCursor.getColumnIndexOrThrow(DatabaseHelper.col_StartDate));
                String field4 = tblSubscpitionCursor.getString(tblSubscpitionCursor.getColumnIndexOrThrow(DatabaseHelper.col_EndDate));
                String field5 = tblSubscpitionCursor.getString(tblSubscpitionCursor.getColumnIndexOrThrow(DatabaseHelper.col_reminderStatus));

                JSONObject subscriptionObject = new JSONObject();
                try {
                    subscriptionObject.put(DatabaseHelper.col_subs_ID, field1);
                    subscriptionObject.put(DatabaseHelper.col_Pap_ID, field2);
                    subscriptionObject.put(DatabaseHelper.col_StartDate, field3);
                    subscriptionObject.put(DatabaseHelper.col_EndDate, field4);
                    subscriptionObject.put(DatabaseHelper.col_reminderStatus, field5);


                    subscriptionArray.put(subscriptionObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONArray deliveryArray = new JSONArray();
            while (tbldeliveryCursor.moveToNext()) {
                String field1 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_id));
                String field2 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_sub_id));
                String field3 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_dis_id));
                String field4 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_timestamp));
                String field5 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_status));
                String field6 = tbldeliveryCursor.getString(tbldeliveryCursor.getColumnIndexOrThrow(DatabaseHelper.col_del_note));

                JSONObject deliveryObject = new JSONObject();
                try {
                    deliveryObject.put(DatabaseHelper.col_del_id, field1);
                    deliveryObject.put(DatabaseHelper.col_del_sub_id, field2);
                    deliveryObject.put(DatabaseHelper.col_del_dis_id, field3);
                    deliveryObject.put(DatabaseHelper.col_del_timestamp, field4);
                    deliveryObject.put(DatabaseHelper.col_del_status, field5);
                    deliveryObject.put(DatabaseHelper.col_del_note, field6);

                    deliveryArray.put(deliveryObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // Add the arrays to the JSON object
            try {
                jsonObject.put(DatabaseHelper.tbl_subscriber, subscriberArray);
                jsonObject.put(DatabaseHelper.tbl_newspaper,NewspaperArray);
                jsonObject.put(DatabaseHelper.tbl_subscription, subscriptionArray);
                jsonObject.put(DatabaseHelper.tbl_delivery, deliveryArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tblSubscriberCursor.close();
            tblNewspaperCursor.close();
            tblSubscpitionCursor.close();
            tbldeliveryCursor.close();
            db.close();
            jsonString = jsonObject.toString();
        Toast.makeText(this, "JSON string created successful", Toast.LENGTH_LONG).show();
            return jsonString;
    }
    private class SendAsynTaskToServer extends AsyncTask<String, Void, Boolean>{
        private static final String SERVER_URL = "http://"+ServerSignIn.localhost+"/XamppProjects/MiniProjectServerFiles/MiniProjectServerFiles/androidJSONdata.php";
        @Override
        protected Boolean doInBackground(String... strings) {
            String jsonString = strings[0];
            boolean success = false;
            try{
                // Create URL object
                URL url = new URL(SERVER_URL);

                // Create connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Set request headers
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                // Write JSON string to the connection's output stream
                OutputStream outputStream = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                writer.write(jsonString);
                writer.flush();
                writer.close();
                outputStream.close();

                // Check if the request was successful (response code 200)
                int responseCode = connection.getResponseCode();
                success = (responseCode == HttpURLConnection.HTTP_OK);

                connection.disconnect();
            }catch(IOException e){
                Log.d(TAG, "doInBackground: "+e.getMessage());
            }
            return success;
        }
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "JSON sent successfully");
                Toast.makeText(Home.this, "Data Sent and Sync Completed", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Failed to send JSON");
            }
        }
    }

    public void DeliveryHistory(View v){
        Intent i = new Intent(this,DeliveryHistoryActivity.class);
        this.startActivity(i);
    }

    public void changepassword(View v){
        Intent i = new Intent(this,ChangeUserPassword.class);
        this.startActivity(i);
    }


}