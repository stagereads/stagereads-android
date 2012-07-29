package com.econify.stagereads.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.R;
import com.econify.stagereads.shop.BillingService;

public class ShopFragment extends SherlockFragment {

    BillingService mBillingService;

    ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopfragment, null);

        return view;
    }
}
