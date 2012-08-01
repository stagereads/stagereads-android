package com.econify.stagereads.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.econify.stagereads.R;
import com.econify.stagereads.shop.BillingService;
import com.econify.stagereads.shop.Consts;
import com.econify.stagereads.shop.PurchaseObserver;
import com.econify.stagereads.shop.ResponseHandler;

public class ShopFragment extends SherlockFragment implements View.OnClickListener {

    TextView mSubscriptionStatus;
    Button mSubscribeButton;

    BillingService mBillingService;

    boolean mSubscribed = false;

    SubscriptionPurchaseObserver mObserver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.shopfragment, null);

        mSubscribeButton = (Button) view.findViewById(R.id.subscribebutton);
        mSubscribeButton.setOnClickListener(this);
        mSubscriptionStatus = (TextView) view.findViewById(R.id.subscriptionstatus);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler mHandler = new Handler();
        mObserver = new SubscriptionPurchaseObserver(mHandler);
        ResponseHandler.register(mObserver);
    }

    @Override
    public void onPause() {
        super.onPause();

        ResponseHandler.unregister(mObserver);
    }

    @Override
    public void onClick(View view) {
        if (view == mSubscribeButton) {
            if (mSubscribed) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=com.econify.stagereads"));
                startActivity(intent);
            } else {
                mBillingService.requestPurchase("3c2b0083a748dc9cfad7e068e607162521432208", Consts.ITEM_TYPE_SUBSCRIPTION, null);
            }
        }
    }

    public void setBillingService(BillingService billingService) {
        mBillingService = billingService;

    }

    /**
     * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
     * messages to this application so that we can update the UI.
     */
    private class SubscriptionPurchaseObserver extends PurchaseObserver {
        public SubscriptionPurchaseObserver(Handler handler) {
            super(getActivity(), handler);
        }

        @Override
        public void onBillingSupported(boolean supported, String type) {
            if (Consts.DEBUG) {
                Log.i("", "supported: " + supported);
            }
            if (type == null || type.equals(Consts.ITEM_TYPE_INAPP)) {
                if (supported) {
                    //restoreDatabase();
                    //mBuyButton.setEnabled(true);
                    //mEditPayloadButton.setEnabled(true);
                } else {
                    //showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
                }
            } else if (type.equals(Consts.ITEM_TYPE_SUBSCRIPTION)) {
                //mCatalogAdapter.setSubscriptionsSupported(supported);
            } else {
                //showDialog(DIALOG_SUBSCRIPTIONS_NOT_SUPPORTED_ID);
            }
        }

        @Override
        public void onPurchaseStateChange(Consts.PurchaseState purchaseState, String itemId,
                                          int quantity, long purchaseTime, String developerPayload) {
            if (Consts.DEBUG) {
                Log.i("", "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
            }

            if (developerPayload == null) {
                //logProductActivity(itemId, purchaseState.toString());
            } else {
                //logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
            }

            if (purchaseState == Consts.PurchaseState.PURCHASED) {

                //mOwnedItems.add(itemId);

                // If this is a subscription, then enable the "Edit
                // Subscriptions" button.
                /*for (CatalogEntry e : CATALOG) {
                    if (e.sku.equals(itemId) &&
                            e.managed.equals(Managed.SUBSCRIPTION)) {
                        mEditSubscriptionsButton.setVisibility(View.VISIBLE);
                    }
                }
            }
            mCatalogAdapter.setOwnedItems(mOwnedItems);
            mOwnedItemsCursor.requery(); */
            }
        }

        @Override
        public void onRequestPurchaseResponse(BillingService.RequestPurchase request, Consts.ResponseCode
                responseCode) {
            if (Consts.DEBUG) {
                Log.d("", request.mProductId + ": " + responseCode);
            }
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.i("", "purchase was successfully sent to server");
                }
                //logProductActivity(request.mProductId, "sending purchase request");
            } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {
                if (Consts.DEBUG) {
                    Log.i("", "user canceled purchase");
                }
                //logProductActivity(request.mProductId, "dismissed purchase dialog");
            } else {
                if (Consts.DEBUG) {
                    Log.i("", "purchase failed");
                }
                //logProductActivity(request.mProductId, "request purchase returned " + responseCode);
            }
        }

        @Override
        public void onRestoreTransactionsResponse(BillingService.RestoreTransactions request,
                                                  Consts.ResponseCode responseCode) {
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                if (Consts.DEBUG) {
                    Log.d("", "completed RestoreTransactions request");
                }
                // Update the shared preferences so that we don't perform
                // a RestoreTransactions again.
                /*SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
              SharedPreferences.Editor edit = prefs.edit();
              edit.putBoolean(DB_INITIALIZED, true);
              edit.commit();  */
            } else {
                if (Consts.DEBUG) {
                    Log.d("", "RestoreTransactions error: " + responseCode);
                }
            }
        }
    }
}

