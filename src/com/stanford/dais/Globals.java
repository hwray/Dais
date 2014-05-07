package com.stanford.dais; 

import android.app.Application;
import android.content.Context;

class Globals extends Application {

	private static Context context; 

	private float mTimeLeft = 0; 
	private float mTimeRight = 0; 
	
	@Override
	public void onCreate() {
		super.onCreate();
		Globals.context = getApplicationContext(); 
	}
	
	
}