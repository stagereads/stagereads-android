package com.econify.stagereads.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.econify.stagereads.Main;
import com.econify.stagereads.R;
import com.econify.stagereads.shop.BillingService;
import com.econify.stagereads.shop.Consts;

public class ShopFragment extends SherlockFragment implements View.OnClickListener {

    TextView mSubscriptionStatus;
    Button mSubscribeButton;

    BillingService mBillingService;

    boolean created = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopfragment, null);

        mSubscribeButton = (Button) view.findViewById(R.id.subscribebutton);
        mSubscribeButton.setOnClickListener(this);
        mSubscriptionStatus = (TextView) view.findViewById(R.id.subscriptionstatus);

        created = true;

        updateSubscriptionStatus();

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == mSubscribeButton) {
            if (((Main) getActivity()).isSubscribed()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.econify.stagereads"));
                startActivity(intent);
            } else {
                mBillingService.requestPurchase("3c2b0083a748dc9cfad7e068e607162521432208", Consts.ITEM_TYPE_SUBSCRIPTION, null);
            }
        }
    }

    public void updateSubscriptionStatus() {
        if (created) {
            if (((Main) getActivity()).isSubscribed()) {
                mSubscriptionStatus.setText("Subscribed!");
                mSubscribeButton.setText("Unsubscribe");
            } else {
                mSubscriptionStatus.setText("No subscription active, why not sign up?");
                mSubscribeButton.setText("Subscribe");
            }
        }
    }

    public void setBillingService(BillingService billingService) {
        mBillingService = billingService;
        mBillingService.restoreTransactions();

    }


}

