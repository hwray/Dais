package com.stanford.dais;

import java.util.ArrayList;

public class Presentation {
	
	float mLeftHeading;
	float mRightHeading;
	float mCenterHeading;
	
	// Demo - Does not save all headings
	float mCurrentHeading;
	
	// Real Talk
	private ArrayList<Float> headings; 
	
	
	public Presentation(float mLeftHeading, float mRightHeading) {
		this.mLeftHeading = mLeftHeading;
		this.mRightHeading = mRightHeading;
		mCenterHeading = (mLeftHeading + mRightHeading)/2;
	}
	
	public Presentation() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
	}
	
	public void addHeading(float newHeading) {
		headings.add(newHeading);
	}
}
