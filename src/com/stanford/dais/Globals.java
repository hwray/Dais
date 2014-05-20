package com.stanford.dais; 


import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import com.firebase.client.*;

public class Globals extends Application {
	
	public Presentation pres; 
		
	@Override
	public void onCreate() {
		super.onCreate();
		pres = new Presentation(new Firebase("https://dais.firebaseio.com/demo"));
	}
	
	public void clearGlobals() {
		pres.reset();
		
	}
	
}