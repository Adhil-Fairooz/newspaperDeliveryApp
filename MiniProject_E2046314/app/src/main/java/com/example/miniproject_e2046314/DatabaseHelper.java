package com.example.miniproject_e2046314;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 5;
    // subscriber table
    public static final String tbl_subscriber = "subscribers";
    public static final String col_subID = "SubscriberID";
    public static final String col_Fname = "Fname";
    public static final String col_Lname = "Lname";
    public static final String col_Address = "Address";
    public static final String col_longitude = "longitude";
    public static final String col_latitude = "latitude";
    /**********************************************************/

    // newspaper table
    public static final String tbl_newspaper = "newspapers";
    public static final String col_PaperID = "PaperID";
    public static final String col_PaperName = "PaperName";
    public static final String col_Type = "Type";
    public static final String col_papers = "papers";
    public static final String col_Price = "Price";
    /***********************************************************/
    // subscription table
    public static final String tbl_subscription = "subscription";
    public static final String col_subs_ID = "subscriberID";
    public static final String col_Pap_ID = "PaperID";
    public static final String col_StartDate = "StartDate";
    public static final String col_EndDate = "EndDate";
    public static final String col_reminderStatus = "reminderStatus";
    /******************************************************************/
    // delivery table
    public static final String tbl_delivery = "delivery";
    public static final String col_del_id = "DeliveryID";
    public static final String col_del_sub_id = "SubscriberID";
    public static final String col_del_dis_id = "DistributorID";
    public static final String col_del_timestamp = "DeliveryTimeStamp";
    public static final String col_del_status = "DeliveryStatus";
    public static final String col_del_note = "Note";

    /*************************************************************************/
    Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            //creating subscriber Table//
            String sql_create_tbl_subs = "CREATE TABLE " + tbl_subscriber + " (" +
                    col_subID + " TEXT PRIMARY KEY, " +
                    col_Fname + " TEXT, " +
                    col_Lname + " TEXT, " +
                    col_Address + " TEXT, " +
                    col_longitude + " TEXT, " +
                    col_latitude + " Text);";
            db.execSQL(sql_create_tbl_subs);
            String sql_create_tbl_delivery = "CREATE TABLE " + tbl_delivery + "(" +
                    col_del_id + " TEXT PRIMARY KEY,"+
                    col_del_sub_id + " TEXT," +
                    col_del_dis_id + " TEXT," +
                    col_del_timestamp + " DATETIME," +
                    col_del_status + " TEXT," +
                    col_del_note + " TEXT," +
                    "FOREIGN KEY(" + col_del_sub_id + ") REFERENCES " + tbl_subscriber + "(" + col_subID + "));";
            db.execSQL(sql_create_tbl_delivery);
            // creating newspaper table
            String sql_create_tbl_newspaper = "CREATE TABLE " + tbl_newspaper + " (" +
                    col_PaperID + " TEXT PRIMARY KEY, " +
                    col_PaperName + " TEXT, " +
                    col_Type + " TEXT, " +
                    col_papers + " TEXT, " +
                    col_Price + " Text);";
            db.execSQL(sql_create_tbl_newspaper);
            //creating subscription table
            String sql_create_tbl_subscription = "CREATE TABLE " + tbl_subscription + " (" +
                    col_subs_ID + " TEXT,"+
                    col_Pap_ID + " TEXT, " +
                    col_StartDate + " TEXT, " +
                    col_EndDate + " TEXT, " +
                    col_reminderStatus + " TEXT, " +
                    "PRIMARY KEY (" + col_subs_ID + ", " + col_Pap_ID + ")," +
                    "FOREIGN KEY(" + col_subs_ID + ") REFERENCES " + tbl_subscriber + "(" + col_subID + "), " +
                    "FOREIGN KEY(" + col_Pap_ID + ") REFERENCES " + tbl_newspaper + "(" + col_PaperID + "));";
            db.execSQL(sql_create_tbl_subscription);



        }catch (Exception ex){
            Log.d("DatabaseHelper", "onCreate: "+ex.getMessage());
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+tbl_subscriber);
        db.execSQL("DROP TABLE IF EXISTS "+tbl_newspaper);
        db.execSQL("DROP TABLE IF EXISTS "+tbl_subscription);
        db.execSQL("DROP TABLE IF EXISTS "+tbl_delivery);

        onCreate(db);
    }

    public SimpleCursorAdapter populateListviewFromDB() {
        try{
            DatabaseHelper dbhelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            String query = "SELECT 1 _id,d."+DatabaseHelper.col_del_id+",s."+DatabaseHelper.col_Fname+", s."+DatabaseHelper.col_Lname+", s."+DatabaseHelper.col_Address+", d."+DatabaseHelper.col_del_status+", d."+DatabaseHelper.col_del_timestamp+", d."+DatabaseHelper.col_del_note+" " +
                    "FROM "+DatabaseHelper.tbl_subscriber+" s, "+DatabaseHelper.tbl_delivery+" d " +
                    "WHERE s."+DatabaseHelper.col_subID+" = d."+DatabaseHelper.col_del_sub_id+" AND "+DatabaseHelper.col_del_dis_id+" = '"+ServerSignIn.UserID+"'";
            Cursor cursor = db.rawQuery(query, null);

            String[] fieldNames = {DatabaseHelper.col_del_id,DatabaseHelper.col_Fname,DatabaseHelper.col_Lname,DatabaseHelper.col_Address,DatabaseHelper.col_del_status,DatabaseHelper.col_del_timestamp,DatabaseHelper.col_del_note};
            int[] viewIds = {R.id.tvDeliveryID,R.id.tvsubFname,R.id.tvsubLname,R.id.tvaddress,R.id.tvDeliveryStatus,R.id.tvtimeStamp,R.id.tvdelNotes};
            SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    context,
                    R.layout.single_item,
                    cursor,
                    fieldNames,
                    viewIds
            );
            return cursorAdapter;
        }catch (Exception ex){
            Log.d("History", "populateListviewFromDB: "+ ex.getMessage());
        }
        return null;
    }
}
