package com.stanford.dais; 


import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

public class Globals extends Application {
	
	public Presentation pres; 
		
	@Override
	public void onCreate() {
		super.onCreate();
		pres = new Presentation();
	}
	
	public void clearGlobals() {
		pres = new Presentation();
	}
	
}