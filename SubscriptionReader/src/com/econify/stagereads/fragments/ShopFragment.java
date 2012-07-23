package com.econify.stagereads.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.adapters.ShopAdapter;
import com.econify.stagereads.shop.BillingService;
import com.econify.stagereads.shop.ShopClient;
import com.econify.stagereads.shop.ShopDB;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopFragment extends SherlockListFragment {

    BillingService mBillingService;
    ShopClient mShopClient;

    boolean mBillingSupported;

    ShopDB mShopDB;

    String DB_INITIALIZED = "dbinit";

    private boolean isReady = false;

    DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitializeSQLCipher();

        mBillingService = new BillingService();
        mBillingService.setContext(getActivity());
    }

    private void InitializeSQLCipher() {
        mShopDB = ShopDB.getShopDB(getActivity());
        //Cursor cursor = mShopDB.getProducts();
        //getActivity().startManagingCursor(cursor);
        //ListAdapter adapter = new ShopAdapter(getActivity(), cursor);
        //setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if billing is supported.
        if (!mBillingService.checkBillingSupported()) {
            mBillingSupported = false;
        } else {
            mBillingSupported = true;
            mShopClient = ShopClient.initShopClient();
            new LoadItems().execute();
        }

        isReady = true;

        updateBillingSupported();
    }

    @Override
    public void onPause() {
        super.onPause();

        isReady = false;
    }

    private void updateBillingSupported() {
        Log.d("", "Updating billing");
        if (!mBillingSupported) {
            setListAdapter(new ArrayAdapter(getActivity(),
                    android.R.layout.simple_list_item_1));

            setEmptyText("In-app purchases are not supported on your device.");

            setListShown(true);
        }
    }

    private class LoadItems extends AsyncTask<Object, Object, Cursor> {

        boolean dbinit = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            setListShown(false);
        }

        @Override
        protected Cursor doInBackground(Object... params) {


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

                            /*
                            if (productId
                            .equals("7952344b41cacc76e41e1dd458296523e05627d6")) {
                            productId = "android.test.purchased"; }
                                */


                            String name = json.getString("name");
                            mShopDB.insertPurchase(id, hashed_resource, name, description, url, updated, created);
                        }

                    }

                    if (getActivity() != null) {
                        SharedPreferences prefs = getActivity().getPreferences(
                                Context.MODE_PRIVATE);
                        dbinit = prefs.getBoolean(DB_INITIALIZED, false);
                    }

                }
                Cursor cursor = mShopDB.getSubscriptions();

                return cursor;

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Cursor result) {

            if (dbinit == false) {
                mBillingService.restoreTransactions();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Restoring Transactions",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            if (result != null && isReady) {
                ListAdapter adapter = new ShopAdapter(getActivity(), result);
                setListAdapter(adapter);
                setListShown(true);
            }
        }

    }
}
