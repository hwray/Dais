package com.stanford.dais; 


import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

public class Globals extends Application {
	
	public Presentation pres; 
		
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public void clearGlobals() {
		
	}
	
}