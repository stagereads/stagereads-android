package com.econify.stagereads;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import com.actionbarsherlock.app.SherlockActivity;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class PlayReader extends SherlockActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent.hasExtra("book")) {

            String bookName = intent.getStringExtra("book");
                // read epub file
                EpubReader epubReader = new EpubReader();
            Book book = null;
            try {
                book = epubReader.readEpub(openFileInput(bookName));
                // print the first title
                List<Resource> contents = book.getContents();
                Log.d("", "" + contents.size());

                Log.d("", contents.get(0).toString());

                WebView webView = new WebView(this);

                setContentView(webView);

                webView.loadData(new String(contents.get(0).getData()), "text/html", null);

            } catch (IOException e) {
                finish();
            }
        }
        else {
            finish();
        }
    }
}
