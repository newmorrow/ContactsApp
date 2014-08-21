package com.example.contactsapp;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.widget.Toast;

public class EmailSenderFragment extends Fragment {
	
	private static final String[] EMAIL_PROJECTION = { Email.ADDRESS };
	private static final String EMAIL_SELECTION = Data.LOOKUP_KEY + "= ?";

	public void sendEmailToContact(String lookupKey) {
		new AsyncTask<String, Integer, String[]>() {
			@Override
            protected String[] doInBackground(String... params) {
				Cursor cursor = getActivity().getContentResolver().query(
				        Email.CONTENT_URI,
				        EMAIL_PROJECTION,
				        EMAIL_SELECTION,
				        new String[] { params[0] },
				        null);
				String[] emails = new String[cursor.getCount()];
				while (cursor.moveToNext()) {
					emails[cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
				}
				cursor.close();
				return emails;
            }
			
			protected void onPostExecute(String[] result) {
				if (result.length == 0) {
					Toast.makeText(getActivity(), R.string.no_email_text, Toast.LENGTH_SHORT).show();
				} else {
					Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
					mailIntent.setData(Uri.parse("mailto:"));
					mailIntent.putExtra(Intent.EXTRA_EMAIL, result);
					mailIntent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.default_email_subject));
					mailIntent.putExtra(Intent.EXTRA_TEXT, getText(R.string.default_email_text));

					if (mailIntent.resolveActivity(getActivity().getPackageManager()) != null) {
						startActivity(mailIntent);
					} else {
						Toast.makeText(getActivity(), R.string.no_email_apps, Toast.LENGTH_SHORT).show();
					}
				}				
			};
		}.execute(lookupKey);
		
	}
}
