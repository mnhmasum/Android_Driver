package com.netcabs.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.netcabs.interfacecallback.OnRequestComplete;
import com.netcabs.jsonparser.CommunicationLayer;

public class HailedRquestAsyncTask extends AsyncTask<String, Void, Void> {

	private Activity context;
	private ProgressDialog progressDialog;
	private OnRequestComplete callback;
	private String responseStatus;

	public HailedRquestAsyncTask(Activity context, OnRequestComplete callback2) {
		this.context = context;
		this.callback = (OnRequestComplete) callback2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = ProgressDialog.show(context, "", "Loading...", true, false);
	}

	@Override
	protected Void doInBackground(String... params) {
		String func_id = params[0];
		String taxi_id = params[1];
		try {
			responseStatus = CommunicationLayer.getHailedRequestData(func_id, taxi_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		
		try {
	        if ((progressDialog != null) && progressDialog.isShowing()) {
	        	progressDialog.dismiss();
	        }
	    } catch (final IllegalArgumentException e) {
	    } catch (final Exception e) {
	    } finally {
	        progressDialog = null;
	    }  
		
		callback.onRequestComplete(responseStatus);
	}

}
