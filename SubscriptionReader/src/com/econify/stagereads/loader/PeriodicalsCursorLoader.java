package com.econify.stagereads.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import com.econify.stagereads.shop.ShopDB;

public class PeriodicalsCursorLoader extends AsyncTaskLoader<Cursor> {
    public PeriodicalsCursorLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        ShopDB shopDb = ShopDB.getShopDB(getContext());
        return shopDb.getPeriodicals();
    }
}
