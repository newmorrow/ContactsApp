package com.example.contactsapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.widget.Toast;

public class EmailSenderFragment extends DialogFragment implements LoaderCallbacks<Cursor> {

	private static final String[] EMAIL_PROJECTION = { Email.ADDRESS };
	private static final String EMAIL_SELECTION = Data.LOOKUP_KEY + "= ?";
	
	private static final String CONTACT_KEY = "com.example.contactsapp.ContaktLookupKey";

	private String contact;
	
	public EmailSenderFragment(String contact) {
	    this.contact = contact;
    }
	
	public EmailSenderFragment() {
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    if (savedInstanceState != null) {
	        contact = savedInstanceState.getString(CONTACT_KEY);
        }
		getLoaderManager().initLoader(0, null, this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString(CONTACT_KEY, contact);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog dialog = new ProgressDialog(getActivity());
	    dialog.setIndeterminate(true);
	    dialog.setCancelable(false);
	    return dialog;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
		        getActivity(),
		        Email.CONTENT_URI,
		        EMAIL_PROJECTION,
		        EMAIL_SELECTION,
		        new String[] { contact },
		        null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		String[] emails = new String[data.getCount()];
		while (data.moveToNext()) {
			emails[data.getPosition()] = data.getString(data.getColumnIndex(Email.ADDRESS));
		}
		sendEmail(emails);
	}
	
	private void sendEmail(String[] emails) {
		if (emails.length == 0) {
			Toast.makeText(getActivity(), R.string.no_email_text, Toast.LENGTH_SHORT).show();
		} else {
			Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
			mailIntent.setData(Uri.parse("mailto:"));
			mailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
			mailIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.default_email_subject));
			mailIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.default_email_text));

			if (mailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
				startActivity(mailIntent);
			} else {
				Toast.makeText(getActivity(), R.string.no_email_apps, Toast.LENGTH_SHORT).show();
			}
		}
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.remove(this);
		transaction.commitAllowingStateLoss();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// do nothing
	}
}
