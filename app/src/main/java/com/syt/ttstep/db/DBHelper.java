package com.syt.ttstep.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.syt.ttstep.beans.PedometerBean;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import static com.syt.ttstep.ui.activity.HomeActivity.TAG;

/**
 * 对SQLite进行封装
 * 1.数据更新:   void upData2Db(ContentValues values , long dayTime)
 * 2.插入指定Pedometerbean 对象到数据库:  void write2Database(PedometerBean data)
 * 3.按天查询数据:  PedometerBean getByDay
 * 4.以列表的形式获取一页数据:   List<PedometerBean> getFromDb(int offVal)
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String PedometerDbName = "PedometerDB";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "pedometer";
    public static final String ID = "_id";
    public static final String STEPS_COUNT = "stepsCount";
    public static final String CALORIE = "calorie";
    public static final String DISTANCE = "distance";
    public static final String PACE = "pace";
    public static final String SPEED = "speed";
    public static final String START_TIME = "startTime";
    public static final String LAST_STEP_TIME = "lastStepTime";
    public static final String DAY = "day";
    private static final String[] COLUMNS = new String[]{
            ID,
            STEPS_COUNT,
            CALORIE,
            DISTANCE,
            PACE,
            SPEED,
            START_TIME,
            LAST_STEP_TIME,
            DAY

    };
    private PedometerBean bean;


    public DBHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建数据库
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME +
                "("
                + " _id integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL ,"
                + "stepsCount integer ,"
                + "calorie double ,"
                + "distance double DEFAULT NULL ,"
                + "pace integer ,"
                + "speed double ,"
                + "startTime Timestamp DEFAULT NULL ,"
                + "lastTime Timestamp DEFAULT NULL ,"
                + "day Timestamp DEFAULT NULL"
                + ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void write2Database(PedometerBean data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(STEPS_COUNT, data.getStepsCount());
        values.put(CALORIE, data.getStepsCount());
        values.put(DISTANCE, data.getDistance());
        values.put(PACE, data.getPace());
        values.put(SPEED, data.getSpeed());
        values.put(START_TIME, data.getStartTime());
        values.put(LAST_STEP_TIME, data.getLastStepTime());
        values.put(DAY, data.getDay());

        db.insert(DBHelper.TABLE_NAME, null, values);
        db.close();
    }

    public PedometerBean getByDay(long dayTime) {
        /**
         * 以天为单位获取计步数据
         * 注意 return 不可能为null , 但可能为空
         * */
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " select * from "
                + DBHelper.TABLE_NAME
                + " where dat = "
                + String.valueOf(dayTime);
        cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.d(TAG, "DBHelper查询到结果：有" + cursor.getCount() + "条数据");
            //这里应该只有一条数据,所以用if
            if (cursor.moveToNext()) {
                bean = new PedometerBean();
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                long day = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepsCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(day);
                return bean;
            }
        }
        cursor.close();
        db.close();
        return bean;
    }


    public List<PedometerBean> getFromDb(int offVal){
        /**
         * 获取分页数据
         * return 不可能为null ， 但可能为空*/
        //分页,一页数据的个数
        int pageSize = 20;
        Cursor cursor = null;
        SQLiteDatabase db = getWritableDatabase();

        //去除从第offset行开始的pagesize个数据 ， 并逆序排序
        cursor = db.query(TABLE_NAME , null ,null ,null , null ,null ,"day desc limit "
        + String.valueOf(pageSize) + " offset " + String.valueOf(offVal) , null);

        List<PedometerBean> res = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                PedometerBean bean = new PedometerBean();
                bean = new PedometerBean();
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                double calorie = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                double distance = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                double speed = cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                long day = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepsCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(day);
                res.add(bean);
            }
        }
        cursor.close();
        db.close();
        return res;
    }

    public void upData2Db(ContentValues values , long dayTime){
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME , values , " day=? " , new String[]{String.valueOf(dayTime)});
        db.close();
    }

}
