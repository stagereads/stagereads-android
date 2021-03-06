package com.econify.stagereads.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PeriodicalsAdapter extends ResourceCursorAdapter {

    private LayoutInflater mInflater;

    public PeriodicalsAdapter(Context context) {
        super(context, 0, null, 0);
        mInflater = LayoutInflater.from(context);
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

        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        String name = cursor.getString(cursor.getColumnIndex("name"));
        holder.text1.setText(name);

        String downloadDate = cursor.getString(cursor.getColumnIndex("download_date"));
        String updated = cursor.getString(cursor.getColumnIndex("updated"));

        int downloaded = cursor.getInt(cursor.getColumnIndex("downloaded"));
        String status = "";
        if (downloaded > 0) {
            if (downloadDate.equals(updated)) {
                status = "Downloaded";
            } else {
                status = "Update Available";
            }
        } else {
            status = "Available";
        }
        holder.text2.setText(status);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}