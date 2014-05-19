package com.stanford.dais; 


import java.util.ArrayList;

import android.app.Application;
import android.content.Context;

public class Globals extends Application {
	
	public float mHeadingLeft = 0; 
	public float mHeadingRight = 0; 
	public float mHeadingCenter = 0; 
		
	public float mTimeLeft = 0; 
	public float mTimeRight = 0; 
	public float mGazeTime = 0; 

	public int mGazeSide = 0; 
	
	public ArrayList<Float> orientations = new ArrayList<Float>(); 
		
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	public void clearGlobals() {
		mHeadingLeft = 0; 
		mHeadingRight = 0;
		mHeadingCenter = 0; 
		mTimeLeft = 0; 
		mTimeRight = 0; 
		mGazeTime = 0; 
		orientations = new ArrayList<Float>(); 
	}
	
}