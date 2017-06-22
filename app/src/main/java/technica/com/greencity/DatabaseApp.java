package technica.com.greencity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Aman on 6/15/2017.
 */


public class DatabaseApp extends SQLiteOpenHelper {

    private static final String requestsTable = "requestsTable";
    private String SR_NO = "sr_no";
    private String PLANT_TYPE = "plant_type";
    private String PLANT_NAME = "plant_name";
    private String URL_IMAGE = "url_image";
    private String COMPLETE_ADDRESS = "complete_address";
    private String STATUS = "status";
    private String TAG = "DatabaseApp";

    public DatabaseApp(Context context) {
        super(context, "DB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = String.format("create table if not exists %s(%s INTEGER PRIMARY KEY AUTOINCREMENT" +
                        ",%s varchar,%s varchar,%s varchar,%s varchar,%s varchar)", requestsTable, SR_NO, PLANT_TYPE,
                PLANT_NAME
                , URL_IMAGE, COMPLETE_ADDRESS, STATUS);
        db.execSQL(createTable);

    }

    public void insert_inLocal(String plantType, String plantName, String urlImage,
                               String complete_address, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues c = new ContentValues();
        //  c.put("sr_no","0");
        c.put(PLANT_TYPE, plantType);
        c.put(PLANT_NAME, plantName);
        c.put(URL_IMAGE, urlImage);
        c.put(COMPLETE_ADDRESS, complete_address);
        c.put(STATUS, status);
        db.insert(requestsTable, null, c);
        Log.e(TAG, " " + plantType + " and data" + plantName + urlImage + complete_address + "" + status);

    }

    public ArrayList<RequestHelper> getRequestsLocal() {
        Log.d(TAG, "called");
        String sr_no, plantType, plantName, urlImage, completeAddress, status;
        ArrayList<RequestHelper> arrayList = new ArrayList<RequestHelper>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor co = db.query(requestsTable, null, null, null, null, null, null);

        while (co.moveToNext()) {
            sr_no = co.getString(co.getColumnIndex(SR_NO));
            plantType = co.getString(co.getColumnIndex(PLANT_TYPE));
            plantName = co.getString(co.getColumnIndex(PLANT_NAME));
            urlImage = co.getString(co.getColumnIndex(URL_IMAGE));
            completeAddress = co.getString(co.getColumnIndex(COMPLETE_ADDRESS));
            status = co.getString(co.getColumnIndex(STATUS));
            arrayList.add(new RequestHelper(sr_no, plantType, plantName, urlImage, completeAddress, status));
            Log.d(TAG, "got from db" + urlImage);

        }       // Log.d("idLocalMsg", "called finished" + arrayList.get(1));

        return arrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
