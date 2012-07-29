package com.econify.stagereads.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.Main;
import com.econify.stagereads.PlayReader;
import com.econify.stagereads.adapters.PeriodicalsAdapter;

import java.net.MalformedURLException;
import java.net.URL;

public class ReadFragment extends SherlockListFragment {

    private boolean isReady = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void updateBooks(Context context, Cursor cursor) {
        PeriodicalsAdapter adapter = new PeriodicalsAdapter(context, cursor);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, final long id) {

        Cursor item = (Cursor) l.getItemAtPosition(position);
        final String urlString = item.getString(item.getColumnIndex("url"));

        int downloaded = item.getInt(item.getColumnIndex("downloaded"));
        String name = item.getString(item.getColumnIndex("name"));
        String description = item.getString(item.getColumnIndex("description"));

        // Check if the user already owns the item
        if (downloaded < 1) {

            Dialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(name)
                    .setMessage(description)
                    .setPositiveButton("Download", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ((Main) getActivity()).downloadPlay(id, urlString);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create();
            dialog.show();

        } else {
            try {
                URL url = new URL(urlString);
                Intent intent = new Intent(getActivity(), PlayReader.class);
                intent.putExtra("book", url.getPath().substring(url.getPath().lastIndexOf('/') + 1));
                startActivity(intent);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
