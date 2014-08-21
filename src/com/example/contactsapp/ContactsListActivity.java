package com.example.contactsapp;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactsapp.PermissionDialogFragment.PermissionsDialogListener;

import static android.provider.ContactsContract.*;

public class ContactsListActivity extends ListActivity implements PermissionsDialogListener, LoaderCallbacks<Cursor> {

	private static final String[] FROM_COLUMNS = { Contacts.DISPLAY_NAME_PRIMARY, Contacts.PHOTO_THUMBNAIL_URI };
	private static final int[] TO_IDS = { R.id.contact_name, R.id.contact_img };

	private static final String[] PROJECTION = {
	        Contacts._ID,
	        Contacts.LOOKUP_KEY,
	        Contacts.DISPLAY_NAME_PRIMARY,
	        Contacts.PHOTO_THUMBNAIL_URI
	};

	private static final String[] EMAIL_PROJECTION = { Email.ADDRESS };
	private static final String EMAIL_SELECTION = Data.LOOKUP_KEY + "= ?";

	private ContactsCursorAdapter listAdapter;

	private ProgressBar buildProgressBar() {
		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progressBar.setIndeterminate(true);
		return progressBar;
	}

	private TextView buildEmptyPlaceholder() {
		TextView textView = new TextView(this);
		textView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		textView.setText(getText(R.string.empty_contacts));
		textView.setTextSize(20);
		return textView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			PermissionDialogFragment permissionDialog = new PermissionDialogFragment();
			permissionDialog.setPermissionListener(this);
			permissionDialog.show(getFragmentManager(), "permissions");
        }
		setEmptyView(buildProgressBar());

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
		setEmptyView(buildEmptyPlaceholder());
		listAdapter.swapCursor(data);
	}

	private void setEmptyView(View newEmptyView) {
		ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
		View emptyView = getListView().getEmptyView();
		if (emptyView != null) {
			root.removeView(emptyView);
		}
		root.addView(newEmptyView);
		getListView().setEmptyView(newEmptyView);
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
		String[] emails = getContactEmails(lookupKey);
		if (emails.length == 0) {
			Toast.makeText(this, R.string.no_email_text, Toast.LENGTH_SHORT).show();
		} else {
			Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
			mailIntent.setData(Uri.parse("mailto:"));
			mailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
			mailIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.default_email_subject));
			mailIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.default_email_text));

			if (mailIntent.resolveActivity(getPackageManager()) != null) {
				startActivity(mailIntent);
			}
		}
	}

	private String[] getContactEmails(String lookupKey) {
		Cursor cursor = getContentResolver().query(
		        Email.CONTENT_URI,
		        EMAIL_PROJECTION,
		        EMAIL_SELECTION,
		        new String[] { lookupKey },
		        null
		        );
		String[] emails = new String[cursor.getCount()];
		while (cursor.moveToNext()) {
			emails[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
		}
		cursor.close();
		return emails;
	}
}
