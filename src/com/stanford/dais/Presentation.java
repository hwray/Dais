package com.stanford.dais;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.firebase.client.*;

public class Presentation {
	
	public float mLeftHeading;
	public float mRightHeading;
	public float mCenterHeading;
	public ArrayList<Float> orientations;
	private Firebase connection;
	
	// Demo - Does not save all headings
	public float mCurrentHeading;
	
	// Real Talk
	private ArrayList<Float> headings; 
	
	public Presentation(Firebase connection) {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		orientations = new ArrayList<Float>();
		this.connection = connection;
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
	
	public void pushOnline() {
		PresentationRunnable run = new PresentationRunnable(this);
		Thread t = new Thread(run);
		t.start();
	}
	
	class PresentationRunnable implements Runnable {
		
		private Presentation pres;
		public PresentationRunnable(Presentation pres) {
			this.pres = pres;
		}
		@Override
		public void run() {
			Firebase childConnection = connection.push();
			childConnection.setValue(pres.toMap());
		}
		
	}
}
