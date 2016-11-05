package com.ypc.accelerometer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by user on 2016/10/22.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    public  static final String DATABASE_NAME="Sensor.db";
    public static final String TABLE_NAME="acc_table";
    public static final String COL_1="axisX";
    public static final String COL_2="axisY";
    public static final String COL_3="axisZ";
    public static final String COL_4="magnitude";
    public static final String SQL_CREATE_ACC="CREATE TABLE "+TABLE_NAME+" ( "+COL_1+" REAL, "+COL_2+" REAL, "+COL_3+" REAL,"+COL_4+" REAL)";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null , 3);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACC);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAccData(double x,double y,double z,double m){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_1,x);
        contentValues.put(COL_2,y);
        contentValues.put(COL_3,z);
        contentValues.put(COL_4,m);
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1)
            return false;
        else
            return true;
    }

    public Cursor getAccData(){
        SQLiteDatabase db=this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }
}
