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
	
	public float mLeftTime; 
	public float mRightTime;  
	
	public float mGazeTime;  
	public int mGazeSide;  
	
	public int numSteps; 
		
	// Demo - Does not save all headings
	public float mCurrentHeading;
	
	// Real Talk
	public ArrayList<Float> headings; 
	
	public Presentation(Firebase connection) {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		orientations = new ArrayList<Float>();
		this.connection = connection;
		mLeftTime = 0; 
		mRightTime = 0; 
		mGazeTime = 0; 
		mGazeSide = 0; 
		numSteps = 0; 
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
