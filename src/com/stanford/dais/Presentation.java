package com.stanford.dais;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.firebase.client.*;

public class Presentation {
	
	private Firebase connection;
	
	public String identifier;
	public float mLeftHeading; 
	public float mRightHeading; 
	public float mCenterHeading;
	
	public float mLeftTime; 
	public float mRightTime;  
	
	public float mGazeTime;  
	public int mGazeSide;
	
	public double mFloorVolume; 
	public double mSpeechVolume; 
	public double mMumbleVolume; 
	
	public ArrayList<Float> orientations;	

	public ArrayList<Double> decibels; 
	
	public int numSteps; 
	
	// Real Talk
	public ArrayList<Float> headings; 
	
	public Presentation(String identifier) {
		this.identifier = identifier;
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		mLeftTime = 0; 
		mRightTime = 0; 
		mGazeTime = 0; 
		mGazeSide = 0; 
		mFloorVolume = 0; 
		mSpeechVolume = 0; 
		mMumbleVolume = 0; 
		numSteps = 0; 
		headings = new ArrayList<Float>();
		decibels = new ArrayList<Double>(); 
	}
	
	public void reset() {
		mLeftHeading = 0;
		mRightHeading = 0;
		mCenterHeading = 0;
		mLeftTime = 0; 
		mRightTime = 0; 
		mGazeTime = 0; 
		mGazeSide = 0; 
		mFloorVolume = 0; 
		mSpeechVolume = 0; 
		mMumbleVolume = 0; 
		numSteps = 0; 
		headings = new ArrayList<Float>();
		decibels = new ArrayList<Double>(); 
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
			Firebase connection = new Firebase("https://dais.firebaseio.com/" + identifier);
			Firebase childConnection = connection.push();
			childConnection.setValue(pres.toMap());
			//pres.reset();
		}
		
	}
}
