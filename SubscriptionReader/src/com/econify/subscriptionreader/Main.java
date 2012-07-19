package com.econify.subscriptionreader;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.econify.subscriptionreader.fragments.ReadFragment;
import com.econify.subscriptionreader.fragments.ShopFragment;

public class Main extends SherlockFragmentActivity implements ActionBar.TabListener {

    ActionBar.Tab readTab;
    ActionBar.Tab shopTab;

    int tabPos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        readTab = actionBar.newTab();
        readTab.setText(R.string.read);
        readTab.setTabListener(this);
        actionBar.addTab(readTab);

        shopTab = actionBar.newTab();
        shopTab.setText(R.string.shop);
        shopTab.setTabListener(this);
        actionBar.addTab(shopTab);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab == readTab) {
            tabPos = 0;
            ft.replace(android.R.id.content, new ReadFragment());
        } else if (tab == shopTab) {
            tabPos = 1;
            ft.replace(android.R.id.content, new ShopFragment());
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}
