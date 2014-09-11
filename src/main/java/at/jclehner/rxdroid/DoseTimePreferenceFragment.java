/**
 * RxDroid - A Medication Reminder
 * Copyright (C) 2011-2014 Joseph Lehner <joseph.c.lehner@gmail.com>
 *
 *
 * RxDroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version. Additional terms apply (see LICENSE).
 *
 * RxDroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RxDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package at.jclehner.rxdroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.support.v4.view.MenuItemCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DoseTimePreferenceFragment extends PreferenceFragment
{
	private boolean mIsFirstLaunch;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mIsFirstLaunch = getActivity().getIntent().getBooleanExtra(DrugEditActivity2.EXTRA_IS_FIRST_LAUNCH, false);
		addPreferencesFromResource(R.xml.dose_times);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle paramBundle)
	{
		if(mIsFirstLaunch)
			return inflater.inflate(R.layout.activity_dose_time_settings, root, false);

		return super.onCreateView(inflater, root, paramBundle);
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if(!Settings.wasDisplayedOnce("license_info"))
		{
			final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
			ab.setCancelable(false);
			ab.setMessage(Html.fromHtml(
					"<center><h1>RxDroid</h1></center>\n" +
					"<small><p>&copy; 2011&ndash;2014&nbsp;&nbsp;Joseph C. Lehner</p>\n" +
					"<p><tt>This program comes with ABSOLUTELY NO WARRANTY.\n" +
					"This is free software, and you are welcome to redistribute it\n" +
					"under the terms of the <a href=\"http://www.gnu.org/licenses/gpl-3.0.html\">" +
					"GNU GPLv3</a>; additional terms apply.</tt></p></small>"));
			ab.setPositiveButton(android.R.string.ok, new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Settings.setDisplayedOnce("license_info");
				}
			});

			final AlertDialog d = ab.create();
			d.setOnShowListener(new DialogInterface.OnShowListener()
			{
				@Override
				public void onShow(DialogInterface dialog)
				{
					try
					{
						((TextView) d.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
					}
					catch(RuntimeException e)
					{
						// ignore
					}
				}
			});

			d.show();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		MenuItem item = menu.add(R.string._title_pref_restore_defaults);
		item.setIcon(android.R.drawable.ic_menu_revert);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				final AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
				ab.setIcon(android.R.drawable.ic_dialog_alert);
				ab.setTitle(R.string._title_warning);
				ab.setMessage(R.string._title_restore_default_settings);
				ab.setNegativeButton(android.R.string.cancel, null);
				/////////////////////
				ab.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Settings.putString("time_morning", getString(R.string.pref_default_time_morning));
						Settings.putString("time_noon", getString(R.string.pref_default_time_noon));
						Settings.putString("time_evening", getString(R.string.pref_default_time_evening));
						Settings.putString("time_night", getString(R.string.pref_default_time_night));

						getPreferenceScreen().removeAll();
						addPreferencesFromResource(R.xml.dose_times);
					}
				});

				ab.show();

				return true;
			}
		});

		if(Version.SDK_IS_PRE_HONEYCOMB && true)
		{
			item.setIcon(R.drawable.ic_action_undo);
			MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		else
			MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_NEVER);
	}

	public void onSaveButtonClicked(View view)
	{
		Settings.putBoolean(Settings.Keys.IS_FIRST_LAUNCH, false);

		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setClass(getActivity(), DrugListActivity2.class);
		startActivity(intent);

		getActivity().finish();
	}
}