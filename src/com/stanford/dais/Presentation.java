package com.stanford.dais;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Presentation {
	
	public float mLeftHeading;
	public float mRightHeading;
	public float mCenterHeading;
	public ArrayList<Float> orientations;
	
	// Demo - Does not save all headings
	public float mCurrentHeading;
	
	// Real Talk
	private ArrayList<Float> headings; 
	
	public Presentation() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		orientations = new ArrayList<Float>();
	}
	
	public void reset() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		orientations = new ArrayList<Float>();
	}
	
	public void addHeading(float newHeading) {
		headings.add(newHeading);
	}
	
	public Map<String, Presentation> toMap() {
		Map<String, Presentation> map = new HashMap<String, Presentation>();
		map.put("presentation", this);
		return map;
	}
}
