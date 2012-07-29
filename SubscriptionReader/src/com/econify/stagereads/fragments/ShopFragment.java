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
import com.econify.stagereads.R;
import com.econify.stagereads.shop.BillingService;
import com.econify.stagereads.shop.Consts;

public class ShopFragment extends SherlockFragment implements View.OnClickListener {

    TextView mSubscriptionStatus;
    Button mSubscribeButton;

    BillingService mBillingService;

    boolean mSubscribed = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopfragment, null);

        mSubscribeButton = (Button) view.findViewById(R.id.subscribebutton);
        mSubscribeButton.setOnClickListener(this);
        mSubscriptionStatus = (TextView) view.findViewById(R.id.subscriptionstatus);
        return view;
    }


    @Override
    public void onClick(View view) {
        if (view == mSubscribeButton) {
            if (mSubscribed) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.econify.stagereads"));
                startActivity(intent);
            } else {
                mBillingService.requestPurchase("", Consts.ITEM_TYPE_SUBSCRIPTION, null);
            }
        }
    }

    public void setBillingService(BillingService billingService) {
        mBillingService = billingService;
    }
}
