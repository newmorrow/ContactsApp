package com.example.contactsapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PermissionDialogFragment extends DialogFragment {
	
	private PermissionsDialogListener listener;
	
	public void setPermissionListener(PermissionsDialogListener listener) {
		this.listener = listener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setMessage(R.string.permission_question);
	    builder.setPositiveButton(R.string.permission_accept, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) {
					listener.onAcceptPermission();
                }
			}
		});
	    builder.setNegativeButton(R.string.permission_decline, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) {
					listener.onDenclinePermission();
				}
			}
		});
	    return builder.create();
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		if (listener != null) {
			listener.onDenclinePermission();
		}
	}
	
	public interface PermissionsDialogListener {
		void onAcceptPermission();
		void onDenclinePermission();
	}
}
