package com.econify.stagereads;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayReader extends SherlockFragmentActivity {

    ViewPager mViewPager;

    ShopDB mShopDB;

    String mBookId;

    List<Resource> contents;
    List<CharSequence> tocItems;

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

            loadBookContents(book);
            extractResources(book);

            BookPagerAdapter pagerAdapter = new BookPagerAdapter(mBookId, contents, getSupportFragmentManager());
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
                showTOCDialog();
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

    private void showTOCDialog() {
        final CharSequence[] items = tocItems.toArray(new CharSequence[tocItems.size()]);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Table of Contents").setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                  mViewPager.setCurrentItem(i, false);
            }
        }).create();
        dialog.show();
    }

    private void loadBookContents(Book book) {

        contents = new ArrayList<Resource>();
        List<TOCReference> references = book.getTableOfContents().getTocReferences();
        tocItems = new ArrayList<CharSequence>();

        for (TOCReference ref : references) {
            List<TOCReference> ref2 = ref.getChildren();
            if (ref2 != null && ref2.size() > 0) {
                String id = "";
                for (TOCReference ref3 : ref2) {
                    if (ref3.getResource().getId() != id) {
                        contents.add(ref3.getResource());
                        tocItems.add(ref3.getTitle());
                        id = ref3.getResource().getId();
                    }
                }
            } else {
                contents.add(ref.getResource());
                tocItems.add(ref.getTitle());
            }
        }
    }

    private void extractResources(Book book) {

        List<Resource> images = new ArrayList<Resource>();

        Resources ress = book.getResources();
        for (Resource res : ress.getAll()) {
            if (res.getMediaType().getName().contains("image/") || res.getMediaType().getName().contains("text/css")) {
                extractResource(res);
            }
        }
    }

    private void extractResource(Resource res) {

        FileOutputStream fos = null;
        try {
            File bookDir = new File(getFilesDir() + "/" + mBookId  + "-images/");
            if (bookDir.exists() == false) {
                bookDir.mkdirs();
            }
            fos = new FileOutputStream(new File(bookDir, res.getId()));
            fos.write(res.getData());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
