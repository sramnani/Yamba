package com.symantec.yamba;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public void onStop() {
		super.onStop();

		getActivity().sendBroadcast(
				new Intent("com.symantec.yamba.action.UPDATE_INTERVAL"));
	}
}
