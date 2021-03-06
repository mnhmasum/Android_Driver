package com.netcabs.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.netcabs.adapter.PastTripAdapter;
import com.netcabs.asynctask.CheckAvailabilityAsyncTask;
import com.netcabs.asynctask.PastTripAsyncTask;
import com.netcabs.customviews.CustomToast;
import com.netcabs.interfacecallback.OnRequestComplete;
import com.netcabs.internetconnection.InternetConnectivity;
import com.netcabs.utils.ConstantValues;
import com.netcabs.utils.DriverApp;

public class FastTripsActivity extends Activity implements OnItemClickListener, OnClickListener {

	private ListView lstViewPastTrips;
	private boolean isLock = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_fast_trips);
		
		if (!DriverApp.getInstance().isLock()) {
			DriverApp.getInstance().setLock(false);
		}
		
		initViews();
		setListener();
		loadData();
	}

	private void initViews() {
		lstViewPastTrips = (ListView) findViewById(R.id.lst_view_past_trips);
	}

	private void setListener() {
		lstViewPastTrips.setOnItemClickListener(this);
		((Button) findViewById(R.id.btn_back)).setOnClickListener(this);
	}

	private void loadData() {
		
		if (DriverApp.getInstance().getProfileInfo() == null) {
			new CustomToast(FastTripsActivity.this, "Error in profile information, please try again").showToast();
			return;
		}
		
		if(InternetConnectivity.isConnectedToInternet(FastTripsActivity.this)) {
			 new PastTripAsyncTask(FastTripsActivity.this, new OnRequestComplete() {
				@Override
				public void onRequestComplete(String result) {
					
					try {
						if("2001".equals(result)) {
							if(DriverApp.getInstance().getFastTripsInfoList()!= null) {
								PastTripAdapter pastTripAdapter = new PastTripAdapter(FastTripsActivity.this, DriverApp.getInstance().getFastTripsInfoList());
								lstViewPastTrips.setAdapter(pastTripAdapter);
							}else{
								CustomToast customToast = new CustomToast(FastTripsActivity.this, "List is Empty");
								customToast.showToast();
							}
						}
						
					} catch (Exception e) {
						new CustomToast(FastTripsActivity.this, "Error " + e.getMessage()).showToast();
					}
					
				}
			}).execute("1012", DriverApp.getInstance().getDriverInfo().getTaxiId(), DriverApp.getInstance().getProfileInfo().getId());
		} else {
			new CustomToast(FastTripsActivity.this, "Internet problem. Please check internet connection").showToast();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		isLock = true;
		startActivity(new Intent(FastTripsActivity.this, PastTripDetailsActivity.class).putExtra("position", arg2));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		ConstantValues.isBack = false;
		isLock = true;
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		if (isLock) {
			isLock = false;
			
			
			if(InternetConnectivity.isConnectedToInternet(FastTripsActivity.this)) {
				new CheckAvailabilityAsyncTask(FastTripsActivity.this, false, new OnRequestComplete() {
					
					@Override
					public void onRequestComplete(String result) {
						
						try {
							if("2001".equals(result)) {
								Log.i("Available check", "Came to available");
								//new CustomToast(FastTripsActivity.this, "Came to available").showToast();
							
							} else {
								new CustomToast(FastTripsActivity.this, "Data not found").showToast();
							}
						} catch (Exception e) {
							new CustomToast(FastTripsActivity.this, "Error " + e.getMessage()).showToast();
							Log.i("Exception 1005", "***" + e.getMessage());
						}
						
						
					}
				}).execute("1003", DriverApp.getInstance().getDriverInfo().getTaxiId(), "1");
				
			} else {
				new CustomToast(FastTripsActivity.this, ConstantValues.internetConnectionMsg).showToast();
			}
		} else {
			
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if (isLock) {
			isLock = false;
		} else {
			DriverApp.getInstance().setLock(true);
			isLock = true;
			//Make unavailable
			if(InternetConnectivity.isConnectedToInternet(FastTripsActivity.this)) {
				new CheckAvailabilityAsyncTask(FastTripsActivity.this, false, new OnRequestComplete() {
					@Override
					public void onRequestComplete(String result) {
						
						try {
							if("2001".equals(result)) {
								Log.i("Available check", "Gone to unavailable");
								new CustomToast(FastTripsActivity.this, "Gone to unavailable").showToast();
							
							} else {
								new CustomToast(FastTripsActivity.this, "Data not found").showToast();
							}
						} catch (Exception e) {
							new CustomToast(FastTripsActivity.this, "Error " + e.getMessage()).showToast();
							Log.i("Exception 1005", "***" + e.getMessage());
						}
						
					}
				}).execute("1003", DriverApp.getInstance().getDriverInfo().getTaxiId(), "0");
				
			} else {
				new CustomToast(FastTripsActivity.this, ConstantValues.internetConnectionMsg).showToast();
			}
		}
		super.onPause();
	}

}
