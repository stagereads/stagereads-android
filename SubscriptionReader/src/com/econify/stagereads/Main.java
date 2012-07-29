package com.econify.stagereads;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.econify.stagereads.adapters.PeriodicalsAdapter;
import com.econify.stagereads.fragments.ReadFragment;
import com.econify.stagereads.fragments.ShopFragment;
import com.econify.stagereads.shop.BillingService;
import com.econify.stagereads.shop.ShopClient;
import com.econify.stagereads.shop.ShopDB;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class Main extends SherlockFragmentActivity implements ActionBar.TabListener {

    BillingService mBillingService;

    ActionBar.Tab readTab;
    ReadFragment mReadFragment;
    ActionBar.Tab shopTab;
    ShopFragment mShopFragment;

    int tabPos = 0;

    ShopClient mShopClient;
    ShopDB mShopDB;

    ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading Play");
        mProgressDialog.setMax(100);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mReadFragment = new ReadFragment();
        mShopFragment = new ShopFragment();

        readTab = actionBar.newTab();
        readTab.setText(R.string.read);
        readTab.setTabListener(this);
        actionBar.addTab(readTab);

        shopTab = actionBar.newTab();
        shopTab.setText(R.string.shop);
        shopTab.setTabListener(this);
        actionBar.addTab(shopTab);

        InitializeSQLCipher();
        mShopClient = ShopClient.initShopClient();

        mBillingService = new BillingService();
        mBillingService.setContext(this);

        mShopFragment.setBillingService(mBillingService);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab == readTab) {
            tabPos = 0;
            ft.replace(android.R.id.content, mReadFragment);
        } else if (tab == shopTab) {
            tabPos = 1;
            ft.replace(android.R.id.content, mShopFragment);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onResume() {
        super.onResume();

        updatePeriodicalLists();

        new LoadItems().execute();
    }

    private void updatePeriodicalLists() {
        mReadFragment.updateBooks(this, mShopDB.getPeriodicals());
    }

    private void InitializeSQLCipher() {
        mShopDB = ShopDB.getShopDB(this);
        Cursor cursor = mShopDB.getDownloadedPeriodicals();
        this.startManagingCursor(cursor);
        ListAdapter adapter = new PeriodicalsAdapter(this, cursor);
        //setListAdapter(adapter);
    }

    public void downloadPlay(long id, String url) {
         new DownloadFile(id).execute(url);
    }

    private class LoadItems extends AsyncTask<Object, Object, Boolean> {

        boolean dbinit = false;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //setListShown(false);
        }

        @Override
        protected Boolean doInBackground(Object... params) {


            try {
                if (mShopClient != null) {
                    JSONArray data = mShopClient.getProducts();

                    if (data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject json = data.getJSONObject(i);
                            String url = json.getJSONObject("resource")
                                    .getString("url");


                            DateTime updatedAt = fmt.parseDateTime(json.getString("updated_at"));
                            DateTime createdAt = fmt.parseDateTime(json.getString("created_at"));

                            long updated = updatedAt.getMillis();
                            long created = createdAt.getMillis();

                            String description = json.getString("description");

                            String hashed_resource = json.getString("hashed_resource");

                            int id = json.getInt("id");

                            String name = json.getString("name");
                            mShopDB.insertPurchase(id, hashed_resource, name, description, url, updated, created);
                        }

                    }

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) {
                updatePeriodicalLists();
            }
        }

    }

    private class DownloadFile extends AsyncTask<String, Integer, String> {

        long mId;

        public DownloadFile(long id) {
            mId = id;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            try {
                URL url = new URL(sUrl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a typical 0-100% progress bar
                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                String outpurFile = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
                OutputStream output = openFileOutput(outpurFile, Context.MODE_PRIVATE);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                mShopDB.setDownloaded(mId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            updatePeriodicalLists();

            mProgressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]);
        }
    }
}
