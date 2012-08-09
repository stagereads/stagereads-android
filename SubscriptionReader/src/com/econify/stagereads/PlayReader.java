package com.econify.stagereads;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.econify.stagereads.adapters.BookPagerAdapter;
import com.econify.stagereads.shop.ShopDB;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayReader extends SherlockFragmentActivity {

    ViewPager mViewPager;

    ShopDB mShopDB;

    String mBookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bookreader);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();

        if (!intent.hasExtra("bookId")) {
            finish();
            return;
        }

        mBookId = intent.getStringExtra("bookId");
        mShopDB = ShopDB.getShopDB(this);
        Cursor c = mShopDB.getPeriodicalFromResource(mBookId);
        c.moveToFirst();

            // read epub file
            EpubReader epubReader = new EpubReader();
            Book book = null;
            try {
                book = epubReader.readEpub(openFileInput(mBookId));

                BookPagerAdapter pagerAdapter = new BookPagerAdapter(book, getSupportFragmentManager());
                mViewPager.setAdapter(pagerAdapter);

                int page = c.getInt(c.getColumnIndex("page"));
                c.close();
                mViewPager.setCurrentItem(page);

            } catch (IOException e) {
                c.close();
                e.printStackTrace();
                finish();
            }

    }

    @Override
    public void onPause() {
        super.onPause();

        mShopDB.setLastPage(mBookId, mViewPager.getCurrentItem());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Table Of Contents").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed
                // in the Action Bar.
                Intent parentActivityIntent = new Intent(this, Main.class);
                parentActivityIntent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(parentActivityIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
