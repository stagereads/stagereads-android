package com.econify.stagereads.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.econify.stagereads.Util;
import nl.siegmann.epublib.domain.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
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
        return new BookPageFragment(contents.get(i));
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    private class BookPageFragment extends Fragment {

        Resource page;

        public BookPageFragment() {

        }

        public BookPageFragment(Resource resource) {
            page = resource;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            WebView webView = new WebView(getActivity());

            try {
                String pageData = new String(page.getData());
                pageData = updateResource(pageData);
                webView.loadDataWithBaseURL("file://" + getActivity().getFilesDir() + "/" + mBookId + "-images/", pageData, "text/html", null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return webView;

        }
    }

    private String updateResource(String pageData) {

        Document doc = Jsoup.parse(pageData);
        for (Element el : doc.select("img")) {
            String src = el.attr("src");
            src = Util.extractFileName(src);
            el.attr("src", src);
        }
        for (Element el : doc.select("link")) {
            String href = el.attr("href");
            href = Util.extractFileName(href);
            el.attr("href", href);
        }
        return doc.toString();
    }
}
