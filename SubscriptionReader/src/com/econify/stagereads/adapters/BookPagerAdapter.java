package com.econify.stagereads.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.econify.stagereads.fragments.BookPageFragment;
import nl.siegmann.epublib.domain.Resource;

import java.util.List;

public class BookPagerAdapter extends FragmentStatePagerAdapter {

    String mBookId;
    List<Resource> contents;

    public BookPagerAdapter(String bookId, List<Resource> contents, FragmentManager fm) {
        super(fm);

        this.contents = contents;
        mBookId = bookId;
    }

    @Override
    public Fragment getItem(int i) {
        return new BookPageFragment(mBookId, contents.get(i));
    }

    @Override
    public int getCount() {
        return contents.size();
    }


}
