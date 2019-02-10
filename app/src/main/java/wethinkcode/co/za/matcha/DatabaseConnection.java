package wethinkcode.co.za.matcha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnection extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "matcha.db";
    public static final String TABLE_NAME = "users";

    public DatabaseConnection(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "`user_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "`username` VARCHAR(50) UNIQUE NOT NULL," +
                "`email` VARCHAR(100) UNIQUE NOT NULL," +
                "`password` VARCHAR(255) NOT NULL," +
                "`first_name` VARCHAR(50) NOT NULL," +
                "`surname` VARCHAR(50) NOT NULL," +
                "`age` INTEGER(3)," +
                "`gender` VARCHAR(50)," +
                "`sexual_pref` VARCHAR(50)," +
                "`biography` MEDIUMTEXT," +
                "`interests` MEDIUMTEXT," +
                "`location` VARCHAR(50)," +
                "`token` VARCHAR(100)," +
                "`pwtoken` VARCHAR(100)," +
                "`status` INTEGER(1) DEFAULT '0' NOT NULL," +
                "`notify` INTEGER(1) DEFAULT '1' NOT NULL" +
                ")");

        db.execSQL("insert into users (username,email) values ('rheeders','stefan.rheeders@gmail.com')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
