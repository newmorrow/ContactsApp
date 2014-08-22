package com.example.contactsapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.widget.ListView;

import com.example.contactsapp.PermissionDialogFragment.PermissionsDialogListener;

public class ContactsListActivity extends ListActivity implements PermissionsDialogListener, LoaderCallbacks<Cursor> {

	private static final String TAG_PERMISSIONS_DIALOG = "com.example.contactsapp.Permissions";
	private static final String TAG_EMAIL_SEND = "com.example.contactsapp.SendEmail";
	
	private static final String[] FROM_COLUMNS = { Contacts.DISPLAY_NAME_PRIMARY, Contacts.PHOTO_THUMBNAIL_URI };
	private static final int[] TO_IDS = { R.id.contact_name, R.id.contact_img };

	private static final String[] PROJECTION = {
	        Contacts._ID,
	        Contacts.LOOKUP_KEY,
	        Contacts.DISPLAY_NAME_PRIMARY,
	        Contacts.PHOTO_THUMBNAIL_URI
	};

	private ContactsCursorAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.a_contacts_list);
		if (savedInstanceState == null) {
			PermissionDialogFragment permissionDialog = new PermissionDialogFragment();
			permissionDialog.setPermissionListener(this);
			permissionDialog.show(getFragmentManager(), TAG_PERMISSIONS_DIALOG);
        } 
		View progressBar = findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.VISIBLE);

		listAdapter = new ContactsCursorAdapter(
		        this,
		        R.layout.i_contact_item,
		        null,
		        FROM_COLUMNS,
		        TO_IDS,
		        0);
		setListAdapter(listAdapter);

		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
    public void onAcceptPermission() {
		//do nothing
    }

	@Override
    public void onDenclinePermission() {
	    finish(); //finish the activity if permission was declined
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
		        this,
		        Contacts.CONTENT_URI,
		        PROJECTION,
		        null,
		        null,
		        null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		View progressBar = findViewById(R.id.progress_bar);
		progressBar.setVisibility(View.GONE);

		listAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		listAdapter.swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		Cursor cursor = listAdapter.getCursor();
		cursor.moveToPosition(position);
		String lookupKey = cursor.getString(cursor.getColumnIndex(Contacts.LOOKUP_KEY));
		
		EmailSenderFragment emailFragment = new EmailSenderFragment(lookupKey);
		emailFragment.setCancelable(true);
		emailFragment.show(getFragmentManager(), TAG_EMAIL_SEND);
	}
}
