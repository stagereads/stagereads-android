package com.econify.stagereads.shop;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

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
                    .execSQL("CREATE TABLE IF NOT EXISTS subscriptions(id INT UNIQUE, hashed_resource STRING, name STRING, description STRING, url STRING, purchased INT DEFAULT 0, updated int, created int)");

            int version = instance.mDatabase.getVersion();
            if (version < 2) {
                instance.mDatabase.setVersion(2);
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
                .execSQL("INSERT OR REPLACE INTO subscriptions (id, hashed_resource, name, description, url, purchased, updated, created) VALUES ("
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
                        + "(SELECT purchased from subscriptions where id = "
                        + id + ")"
                        + ", "
                        + updated
                        + ", "
                        + created
                        + ")");
    }

    public int updatePurchase(int productId) {

        ContentValues values = new ContentValues();
        values.put("purchased", 1);
        return mDatabase.update("purchases", values, "id=" + productId,
                null);
    }

    public Cursor getSubscriptions() {
            return mDatabase.rawQuery("SELECT * FROM subscriptions", null);
    }
}
