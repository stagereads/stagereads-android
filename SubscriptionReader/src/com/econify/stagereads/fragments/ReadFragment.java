package com.econify.stagereads.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.PlayReader;
import com.econify.stagereads.adapters.PeriodicalsAdapter;
import com.econify.stagereads.shop.ShopClient;
import com.econify.stagereads.shop.ShopDB;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ReadFragment extends AbstractTabFragment {

    private boolean isReady = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Cursor c = (Cursor) l.getItemAtPosition(position);
        String urlString = c.getString(c.getColumnIndex("url"));

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
