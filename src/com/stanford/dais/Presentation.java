package com.stanford.dais;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Presentation {
	
	public float mLeftHeading; 
	public float mRightHeading; 
	public float mCenterHeading;
	
	public float mLeftTime; 
	public float mRightTime;  
	
	public float mGazeTime;  
	public int mGazeSide;  
		
	// Demo - Does not save all headings
	public float mCurrentHeading;
	
	// Real Talk
	public ArrayList<Float> headings; 
	
	public Presentation() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		mLeftTime = 0; 
		mRightTime = 0; 
		mGazeTime = 0; 
		mGazeSide = 0; 
		headings = new ArrayList<Float>();
	}
	
	public void reset() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		mLeftTime = 0; 
		mRightTime = 0; 
		mGazeTime = 0; 
		mGazeSide = 0; 
		headings = new ArrayList<Float>();
	}
	
	public Map<String, Presentation> toMap() {
		Map<String, Presentation> map = new HashMap<String, Presentation>();
		map.put("presentation", this);
		return map;
	}
}
