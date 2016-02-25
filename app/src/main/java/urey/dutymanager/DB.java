package urey.dutymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by urey on 05.02.2016.
 */
public class DB {
    private static final String DB_NAME = "db_Duty_Manager";
    private static final int DB_VERSION = 1;

/*PERIODS*/
public static final String DB_TABLE_PERIODS = "Periods";

    public static final String COL_ID = "_id";

    public static final String COL_PER_YEAR = "Per_Year";
    public static final String COL_PER_MONTH= "Per_Month";
    public static final String COL_PER_SUM = "Per_Sum";
    public static final String COL_PER_NOTES= "Per_Notes";
    public static final String COL_PER_TYPE= "Per_Type";
/*MEMBERS*/
public static final String DB_TABLE_MEMBERS = "Members";

    public static final String COL_MEM_NAME = "Mem_Name";
    public static final String COL_MEM_PASS= "Mem_Pass";
    public static final String COL_MEM_FLAGS = "Mem_Flags";

/*TYPES*/
public static final String DB_TABLE_TYPES = "Types";

    public static final String COL_TYPE_NAME = "Type_Name";
    public static final String COL_TYPE_FLAGS = "Type_Flags";

/*CONSIST*/
public static final String DB_TABLE_CONSIST = "Consist";

    public static final String COL_CON_PERIOD = "Con_Period";
    public static final String COL_CON_MEMBER = "Con_Member";
    public static final String COL_CON_PAYED = "Con_Payed";
    public static final String COL_CON_DUTY = "Con_Duty";

/*SETTINGS*/
public static final String DB_TABLE_SETTINGS = "Settings";

    public static final String COL_SET_PROMO = "Set_Promo";



/*========CREATE STRINGS===========*/
    private static final String DB_CREATE_PERIODS =
            "create table " + DB_TABLE_PERIODS + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_PER_YEAR + " text, " +
                    COL_PER_MONTH + " text, " +
                    COL_PER_SUM + " real, " +
                    COL_PER_NOTES + " text, " +
                    COL_PER_TYPE + " integer" +
                    ");";

    private static final String DB_CREATE_MEMBERS =
            "create table " + DB_TABLE_MEMBERS + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_MEM_NAME + " text, " +
                    COL_MEM_PASS + " text, " +
                    COL_MEM_FLAGS + " integer" +
                    ");";

    private static final String DB_CREATE_TYPES =
            "create table " + DB_TABLE_TYPES + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_TYPE_NAME + " text, " +
                    COL_TYPE_FLAGS + " integer" +
                    ");";

    private static final String DB_CREATE_CONSIST =
            "create table " + DB_TABLE_CONSIST + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_CON_PERIOD + " integer, " +
                    COL_CON_MEMBER + " integer, " +
                    COL_CON_PAYED + " real, " +
                    COL_CON_DUTY + " real" +
                    ");";

    private static final String DB_CREATE_SETTINGS =
            "create table " + DB_TABLE_SETTINGS + "(" +
                    COL_ID + " integer primary key autoincrement, " +
                    COL_SET_PROMO + " text" +
                    ");";

    private final Context mCtx;


    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }

    // получить все данные из таблицы DB_TABLE
    public Cursor getAllMembers() {
        Log.d("TestLog", "ongetAllMembers DB start");
        String s = "select * from "+DB_TABLE_MEMBERS +
                " where "+COL_ID+"<>1";
        return mDB.rawQuery(s,null);
        //return mDB.query(DB_TABLE_MEMBERS, null, null, null, null, null, null);
    }

    public Cursor getAllPeriods() {
        Log.d("TestLog", "ongetAllPeriods DB start");
        return mDB.query(DB_TABLE_PERIODS, null, null, null, null, null, null);
    }

    public Cursor getConsist(int period, int type) {
        Log.d("TestLog", "ongetConsist DB start");
        String sqlConsist = "select C._id, M." + COL_MEM_NAME +" as "+ COL_MEM_NAME
                + ", C." + COL_CON_DUTY + " as "+ COL_CON_DUTY
                + ", C." + COL_CON_PAYED + " as "+ COL_CON_PAYED
                + ", C." + COL_CON_MEMBER + " as "+ COL_CON_MEMBER
                + " from " + DB_TABLE_CONSIST + " as C, " + DB_TABLE_MEMBERS + " as M"
                + " where C." + COL_CON_MEMBER + "=" + "M." + COL_ID
                + " and C." + COL_CON_PERIOD + " = ?";
        return mDB.rawQuery(sqlConsist, new String[]{String.valueOf(period)});
    }

    public Cursor getChooseConsist(int period) {
        Log.d("TestLog", "getChooseConsist DB start");
        String sqlConsist;
       if(period>0) {
           sqlConsist = "select _id, " + COL_MEM_NAME
                   + " from " + DB_TABLE_MEMBERS
                   + " where _id not in (select distinct " + COL_CON_MEMBER
                   + " from " + DB_TABLE_CONSIST + " where " + COL_CON_PERIOD
                   + "=?)"
                + " and "+COL_ID+"<>1";
           return mDB.rawQuery(sqlConsist,new String[]{String.valueOf(period)});
       }
        else
       {
           sqlConsist = "select _id, " + COL_MEM_NAME
                   + " from " + DB_TABLE_MEMBERS;
           return mDB.rawQuery(sqlConsist,null);
       }

    }


    // добавить запись в DB_TABLE_PERIODS
    public void addPeriod(String year, String month, double sum, String notes, int type) {
        Log.d("TestLog", "onaddPeriod DB start");
        ContentValues cv = new ContentValues();
        cv.put(COL_PER_MONTH, month);
        cv.put(COL_PER_YEAR, year);
        cv.put(COL_PER_SUM, sum);
        cv.put(COL_PER_NOTES, notes);
        cv.put(COL_PER_TYPE, type);
        mDB.insert(DB_TABLE_PERIODS, null, cv);
        Log.d("TestLog", "addPeriod DB end");
    }

    // добавить запись в DB_TABLE_MEMBERS
    public void addMember(String name, String pass, int flags) {
        Log.d("TestLog", "addMember DB start");
        ContentValues cv = new ContentValues();
        cv.put(COL_MEM_NAME, name);
        cv.put(COL_MEM_PASS, pass);
        cv.put(COL_MEM_FLAGS, flags);
        mDB.insert(DB_TABLE_MEMBERS, null, cv);
        Log.d("TestLog", "addMember DB end");
    }

    // добавить запись в DB_TABLE_CONSIST
    public void addConsist(int period, int member, double duty, double payed) {
        Log.d("TestLog", "addConsist DB start");
        ContentValues cv = new ContentValues();
        cv.put(COL_CON_PERIOD, period);
        cv.put(COL_CON_MEMBER, member);
        cv.put(COL_CON_DUTY, duty);
        cv.put(COL_CON_PAYED, payed);
        mDB.insert(DB_TABLE_CONSIST, null, cv);
        Log.d("TestLog", "addConsist DB end");
    }

    public void UpConsist(int id, double duty, double payed) {
        Log.d("TestLog", "UpConsist DB start");
        String sUp = "update "+DB_TABLE_CONSIST+" set "+
                COL_CON_PAYED +"="+String.valueOf(payed)+
                ", "+COL_CON_DUTY+"="+String.valueOf(duty)+
                " where "+COL_ID+"="+String.valueOf(id);
        mDB.execSQL(sUp);
        Log.d("TestLog", "UpConsist DB end");
    }

    // удалить запись из DB_TABLE
    public void delRec(String table, long id)
    {
        Log.d("TestLog", "DB delRec Table=" + table + " id=" + id);
        mDB.delete(table, COL_ID + " = " + id, null);
        if (table == DB_TABLE_PERIODS)
        {
            mDB.delete(DB_TABLE_CONSIST, COL_CON_PERIOD + " = " + id, null);
        }
        Log.d("TestLog", "DB delRec OK");
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                        int version) {
            super(context, name, factory, version);
        }

        // создаем и заполняем БД
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("TestLog", "onCreate DB start");
            db.execSQL(DB_CREATE_CONSIST);
            db.execSQL(DB_CREATE_MEMBERS);
            db.execSQL(DB_CREATE_PERIODS);
            db.execSQL(DB_CREATE_SETTINGS);
            db.execSQL(DB_CREATE_TYPES);

            ContentValues cv = new ContentValues();

            cv.put(COL_MEM_NAME, "developer");
            cv.put(COL_MEM_PASS, "urey123`");
            cv.put(COL_MEM_FLAGS, 0xFF);

            db.insert(DB_TABLE_MEMBERS, null, cv);

            cv.put(COL_MEM_NAME, "First");
            cv.put(COL_MEM_PASS, "urey123`");
            cv.put(COL_MEM_FLAGS, 0xFF);

            db.insert(DB_TABLE_MEMBERS, null, cv);

            cv.put(COL_MEM_NAME, "Second");
            cv.put(COL_MEM_PASS, "urey123`");
            cv.put(COL_MEM_FLAGS, 0xFF);

            db.insert(DB_TABLE_MEMBERS, null, cv);

            cv.put(COL_MEM_NAME, "Third");
            cv.put(COL_MEM_PASS, "urey123`");
            cv.put(COL_MEM_FLAGS, 0xFF);

            db.insert(DB_TABLE_MEMBERS, null, cv);


            Log.d("TestLog", "onCreate DB end");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
