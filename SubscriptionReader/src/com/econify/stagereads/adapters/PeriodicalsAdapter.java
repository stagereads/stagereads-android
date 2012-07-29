package com.econify.stagereads.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class PeriodicalsAdapter implements ListAdapter {

    private Cursor mCursor;
    private LayoutInflater mInflater;

    public PeriodicalsAdapter(Context context, Cursor cursor) {
        mCursor = cursor;

        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        mCursor.moveToPosition(position);
        return mCursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(mCursor.getColumnIndex("_id"));
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView text1;
        TextView text2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(
                    android.R.layout.simple_list_item_2, null);

            holder.text1 = (TextView) convertView
                    .findViewById(android.R.id.text1);

            holder.text2 = (TextView) convertView
                    .findViewById(android.R.id.text2);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        mCursor.moveToPosition(position);
        String name = mCursor.getString(mCursor.getColumnIndex("name"));
        holder.text1.setText(name);

        int downloaded = mCursor.getInt(mCursor.getColumnIndex("downloaded"));
        holder.text2.setText("" + downloaded);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

}