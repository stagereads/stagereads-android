package com.econify.stagereads.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.econify.stagereads.R;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

import java.io.IOException;
import java.util.List;

public class BookPagerAdapter extends FragmentPagerAdapter {

    Book mBook;
    List<Resource> contents;

    public BookPagerAdapter(Book book, FragmentManager fm) {
        super(fm);

        mBook = book;

        contents = book.getContents();
    }

    @Override
    public Fragment getItem(int i) {
        return new BookPageFragment(contents.get(i));
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    private class BookPageFragment extends Fragment {

        Resource page;

        public BookPageFragment(Resource resource) {
            page = resource;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.bookpagefragment, null);

            WebView webView = (WebView) view.findViewById(R.id.webview);

            try {
                webView.loadData(new String(page.getData()), "text/html", null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;

        }
    }
}
