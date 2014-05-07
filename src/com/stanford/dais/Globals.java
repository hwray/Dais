package com.stanford.dais; 


import android.app.Application;
import android.content.Context;

public class Globals extends Application {
	
	public float mTimeLeft = 0; 
	public float mTimeRight = 0; 
	
	private Context context; 
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
}