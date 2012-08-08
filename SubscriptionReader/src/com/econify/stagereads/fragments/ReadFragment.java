package com.econify.stagereads.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockListFragment;
import com.econify.stagereads.Main;
import com.econify.stagereads.PlayReader;
import com.econify.stagereads.adapters.PeriodicalsAdapter;
import com.econify.stagereads.loader.PeriodicalsCursorLoader;

public class ReadFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemLongClickListener {

    PeriodicalsAdapter mPeriodicalsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Give some text to display if there is no data.  In a real
        // application this would come from a resource.
        setEmptyText("No Periodicals");

        // Create an empty adapter we will use to display the loaded data.
        mPeriodicalsAdapter = new PeriodicalsAdapter(getActivity());
        setListAdapter(mPeriodicalsAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();

        getListView().setOnItemLongClickListener(this);
    }

    public void updateBooks() {
        if (getActivity() != null) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, final long id) {

        Cursor cursor = (Cursor) l.getItemAtPosition(position);
        int downloaded = cursor.getInt(cursor.getColumnIndex("downloaded"));
        String hashed_resource = cursor.getString(cursor.getColumnIndex("hashed_resource"));

        if (!((Main) getActivity()).isSubscribed()) {
            Toast.makeText(getActivity(), "To read this play head over to the subscribe tab.", Toast.LENGTH_SHORT).show();
        } else if (downloaded < 1) {

            showDownloadDialog(cursor);

        } else {
            Intent intent = new Intent(getActivity(), PlayReader.class);
            intent.putExtra("bookId", hashed_resource);
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        showDownloadDialog((Cursor) adapterView.getItemAtPosition(i));

        return true;
    }

    private void showDownloadDialog(Cursor cursor) {

        final String urlString = cursor.getString(cursor.getColumnIndex("url"));
        final String hashed_resource = cursor.getString(cursor.getColumnIndex("hashed_resource"));

        int downloaded = cursor.getInt(cursor.getColumnIndex("downloaded"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        final long id = cursor.getLong(cursor.getColumnIndex("_id"));

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(name)
                .setMessage(description)
                .setPositiveButton((downloaded > 0) ? "Re-Download" : "Download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((Main) getActivity()).downloadPlay(hashed_resource, urlString);
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new PeriodicalsCursorLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mPeriodicalsAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mPeriodicalsAdapter.swapCursor(null);
    }
}
