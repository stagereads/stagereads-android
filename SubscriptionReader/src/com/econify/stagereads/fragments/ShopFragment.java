package com.econify.stagereads.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.econify.stagereads.Main;
import com.econify.stagereads.shop.BillingService;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ShopFragment extends AbstractTabFragment {

    BillingService mBillingService;

    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, final long id) {
        Cursor item = (Cursor) getListAdapter().getItem(position);

        int downloaded = item.getInt(item.getColumnIndex("downloaded"));
        final String url = item.getString(item.getColumnIndex("url"));
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
                            ((Main) getActivity()).downloadPlay(id, url);
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

        }

    }
}
