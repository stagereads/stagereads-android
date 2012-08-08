package com.econify.stagereads.shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

public class ShopDB {

    private static ShopDB instance;

    private SQLiteDatabase mDatabase;

    public static ShopDB getShopDB(Context context) {
        if (instance == null) {
            instance = new ShopDB();

            context = context.getApplicationContext();

            SQLiteDatabase.loadLibs(context);
            String dbPath = getDBPath(context);

            File dbPathFile = new File(dbPath);
            if (!databaseExists(context)) {
                dbPathFile.getParentFile().mkdirs();
            }

            instance.mDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath,
                    "password", null);
            instance.mDatabase
                    .execSQL("CREATE TABLE IF NOT EXISTS periodicals(_id INT UNIQUE, hashed_resource STRING, name STRING, description STRING, url STRING, updated int, created int, downloaded INT DEFAULT 0)");
            instance.mDatabase
                    .execSQL("CREATE TABLE IF NOT EXISTS subscription(_id STRING UNIQUE, subscribed INT DEFAULT 0)");
            int version = instance.mDatabase.getVersion();
            if (version < 3) {
                instance.mDatabase.setVersion(3);
                instance.mDatabase.execSQL("ALTER TABLE periodicals ADD COLUMN page INT DEFAULT 0");
            }
            if (version < 4) {
                instance.mDatabase.setVersion(4);
                instance.mDatabase.execSQL("ALTER TABLE periodicals ADD COLUMN download_date INT DEFAULT 0");
                instance.mDatabase.execSQL("UPDATE periodicals SET download_date = updated");
            }
        }
        return instance;
    }

    public static boolean databaseExists(Context context) {
        String dbPath = getDBPath(context);

        File dbPathFile = new File(dbPath);
        return dbPathFile.exists();
    }

    private static String getDBPath(Context context) {
        return context.getDatabasePath("purchases.db").getPath();
    }

    public void insertPurchase(int id, String hashed_resource, String name, String description, String url, long updated, long created) {
        mDatabase
                .execSQL("INSERT OR REPLACE INTO periodicals (_id, hashed_resource, name, description, url, updated, created, downloaded, page, download_date) VALUES ("
                        + id
                        + ", '"
                        + hashed_resource
                        + "', '"
                        + name
                        + "', '"
                        + description.replace("'", "''")
                        + "', '"
                        + url
                        + "', "
                        + updated
                        + ", "
                        + created
                        + ", "
                        + "(SELECT downloaded from periodicals where _id = "
                        + id + "), "
                        + "(SELECT page from periodicals where _id = "
                        + id + "), "
                        + "(SELECT download_date from periodicals where _id = "
                        + id + ")"
                        + ")");
    }

    public Cursor getPeriodicals() {
        return mDatabase.rawQuery("SELECT * FROM periodicals", null);
    }

    public Cursor getDownloadedPeriodicals() {
        return mDatabase.rawQuery("SELECT * FROM periodicals WHERE downloaded = 1", null);
    }

    public void setDownloaded(String hashedResource) {
        ContentValues values = new ContentValues();
        values.put("downloaded", 1);
        mDatabase.update("periodicals", values, "hashed_resource = ?", new String[]{hashedResource});
        mDatabase.rawExecSQL("UPDATE periodicals SET download_date = updated WHERE hashed_resource = '" + hashedResource + "'");
    }

    public boolean isSubscribed(String id) {
        Cursor c = mDatabase.rawQuery("SELECT * FROM subscription WHERE _id = '" + id + "'", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            int value = c.getInt(c.getColumnIndex("subscribed"));
            c.close();
            return value > 0;
        }
        c.close();
        return false;
    }

    public void setSubscribed(String id) {
        setSubscribed(id, 1);
    }

    public void setUnsubscribed(String id) {
        setSubscribed(id, 0);
    }

    public void setSubscribed(String id, int value) {
        mDatabase.execSQL("INSERT OR REPLACE INTO subscription (_id, subscribed) VALUES ('"
                + id
                + "', "
                + value
                + ")");
    }

    public Cursor getPeriodicalFromResource(String hashed_resource) {
       return mDatabase.rawQuery("SELECT * FROM periodicals WHERE hashed_resource = '" + hashed_resource + "'", null);
    }

    public void setLastPage(String hashed_resource, int page) {
        ContentValues values = new ContentValues();
        values.put("page", page);
        int result = mDatabase.update("periodicals", values, "hashed_resource = ?", new String[]{hashed_resource});
    }
}
