package com.econify.stagereads.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class BookPageFragment extends Fragment {

    Resource page;
    String mBookId;

    public BookPageFragment() {

    }

    public BookPageFragment(String bookId, Resource resource) {
        page = resource;
        mBookId = bookId;
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
