package com.jtxdriggers.android.ventriloid;

import java.util.ArrayList;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class Main extends Activity {
	
	public static final String SERVICE_RECEIVER = "com.jtxdriggers.android.ventriloid.Main.SERVICE_RECEIVER";

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private ServerAdapter db;
	private ActionBar ab;
	private ProgressDialog dialog;
	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		db = new ServerAdapter(this);
		
		ab = getSupportActionBar();
		ab.setDisplayShowTitleEnabled(false);
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		registerReceiver(serviceReceiver, new IntentFilter(SERVICE_RECEIVER));
		
		if (VentriloidService.isConnected()) {
			startActivity(new Intent(this, Connected.class));
			finish();
		}
	}
    
    @Override
    public void onStart() {
        super.onStart();
        loadServers();
    }
    
    @Override
    public void onDestroy() {
    	unregisterReceiver(serviceReceiver);
    	super.onDestroy();
    }

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.connect).setEnabled(db.getServersCount() > 0);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connect:
    		dialog = new ProgressDialog(Main.this);
			dialog.setMessage("Connecting. Please wait...");
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					stopService(serviceIntent);
				}
			});
			dialog.show();
			
			int serverId = db.getAllServers().get(getSupportActionBar().getSelectedNavigationIndex()).getId();
			getDefaultSharedPreferences().edit().putInt("default", serverId).commit();
			
    		serviceIntent = new Intent(VentriloidService.SERVICE_INTENT).putExtra("id", serverId);
			startService(serviceIntent);
			return true;
		case R.id.manage:
			startActivity(new Intent(this, Manage.class));
			return true;
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void loadServers() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ab.getThemedContext(), R.layout.spinner_item,
			android.R.id.text1, db.getAllServersAsStrings());
		adapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
		ab.setListNavigationCallbacks(adapter, null);

		ArrayList<Server> servers = db.getAllServers();
		for (int i = 0; i < servers.size(); i++) {
			if (servers.get(i).getId() == getDefaultSharedPreferences().getInt("default", -1)) {
				ab.setSelectedNavigationItem(i);
				break;
			}
		}
		
    	invalidateOptionsMenu();
	}
    
    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getShortExtra("type", (short) 0)) {
			case VentriloEvents.V3_EVENT_LOGIN_COMPLETE:
				if (dialog != null && dialog.isShowing())
					dialog.dismiss();
				startActivity(new Intent(Main.this, Connected.class));
				finish();
				break;
			case VentriloEvents.V3_EVENT_LOGIN_FAIL:
			case VentriloEvents.V3_EVENT_ERROR_MSG:
				dialog.dismiss();
				break;
			case -1:
				if (dialog == null || !dialog.isShowing()) {
		    		dialog = new ProgressDialog(Main.this);
					dialog.setMessage("Reconnecting in " + intent.getIntExtra("timer", -1) + "...");
					dialog.setCancelable(true);
					dialog.setOnCancelListener(new OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							sendBroadcast(new Intent(VentriloidService.ACTIVITY_RECEIVER).putExtra("type", VentriloEvents.V3_EVENT_DISCONNECT));
						}
					});
					dialog.show();
				} else {
					dialog.setMessage("Reconnecting in " + intent.getIntExtra("timer", -1) + "...");
				}
				break;
			}
		}
    };

}