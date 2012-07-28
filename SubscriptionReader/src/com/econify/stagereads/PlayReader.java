package com.econify.stagereads;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.econify.stagereads.adapters.BookPagerAdapter;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;

public class PlayReader extends SherlockFragmentActivity {

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bookreader);

        Intent intent = getIntent();

        if (!intent.hasExtra("book")) {
            finish();
            return;
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        String bookName = intent.getStringExtra("book");
        // read epub file
        EpubReader epubReader = new EpubReader();
        Book book = null;
        try {
            book = epubReader.readEpub(openFileInput(bookName));

            BookPagerAdapter pagerAdapter = new BookPagerAdapter(book, getSupportFragmentManager());
            mViewPager.setAdapter(pagerAdapter);

        } catch (IOException e) {
            finish();
        }
    }


}
