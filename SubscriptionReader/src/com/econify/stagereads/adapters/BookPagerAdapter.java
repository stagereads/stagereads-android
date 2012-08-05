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
import nl.siegmann.epublib.domain.TOCReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookPagerAdapter extends FragmentPagerAdapter {

    Book mBook;
    List<Resource> contents;

    public BookPagerAdapter(Book book, FragmentManager fm) {
        super(fm);

        mBook = book;
        contents = new ArrayList<Resource>();
        List<TOCReference> references = book.getTableOfContents().getTocReferences();

        for (TOCReference ref : references) {
            List<TOCReference> ref2 = ref.getChildren();
            if (ref2 != null && ref2.size() > 0) {
                String id = "";
                for (TOCReference ref3 : ref2) {
                    if (ref3.getResource().getId() != id) {
                        contents.add(ref3.getResource());
                        id = ref3.getResource().getId();
                    }
                }
            } else {
                contents.add(ref.getResource());
            }
        }
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
                String pageData = new String(page.getData());
                webView.loadData(pageData, "text/html", null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;

        }
    }
}
