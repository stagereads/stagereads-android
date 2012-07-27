package com.econify.stagereads.fragments;

import android.content.Context;
import android.database.Cursor;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.adapters.PeriodicalsAdapter;

public class AbstractTabFragment extends SherlockListFragment {

    public void updateBooks(Context context, Cursor cursor) {
        PeriodicalsAdapter adapter = new PeriodicalsAdapter(context, cursor);
        setListAdapter(adapter);
    }
}
