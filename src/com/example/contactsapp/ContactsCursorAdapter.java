package com.example.contactsapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class ContactsCursorAdapter extends SimpleCursorAdapter {

	public ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
	    super(context, layout, c, from, to, flags);
    }
	
	@Override
	public void setViewImage(ImageView view, String value) {
	    if (value.isEmpty()) {
	    	view.setImageResource(android.R.drawable.gallery_thumb);
        } else {
        	view.setImageURI(Uri.parse(value));
		}
	}

}
