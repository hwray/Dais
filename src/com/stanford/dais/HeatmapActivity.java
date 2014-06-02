package com.stanford.dais; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HeatmapActivity extends Activity {

	private Globals g; 
	private View mainView; 

	private final int NUM_SEGMENTS = 25; 
	private ArrayList<Double> segmentProportions; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        
        g = (Globals) getApplication(); 
        
        Collections.sort(g.pres.headings, new Comparator<Float>() {
            @Override
            public int compare(Float heading1, Float heading2)
            {
            	if (heading1 < heading2) {
            		return -1; 
            	} else if (heading1 > heading2) {
            		return 1;
            	} else {
            		return 0; 
            	}
            }
        });
       
                
        countOrientationsBySegment(); 
        
        for (int i = 0; i < NUM_SEGMENTS; i++) {
        	colorHeatmapSegment(i);
        }
    }
    
    
    private void countOrientationsBySegment() {
    	ArrayList<Integer> segmentCounters = new ArrayList<Integer>(); 
    	segmentProportions = new ArrayList<Double>(); 
    	int numHeadings = g.pres.headings.size(); 
    	float range = g.pres.mRightHeading - g.pres.mLeftHeading; 
    	
    	float nextBreakpoint = g.pres.mLeftHeading + (range / NUM_SEGMENTS);
    	float nextBreakpointIndex = 1; 
    	int currCounter = 0; 
    	int totalCounter = 0; 
    	for (int i = 0; i < numHeadings; i++) {
    		if (g.pres.headings.get(i) > nextBreakpoint) {
    			segmentCounters.add(currCounter); 
    			currCounter = 0; 
    			nextBreakpointIndex++; 
    			nextBreakpoint = g.pres.mLeftHeading + ((float)nextBreakpointIndex * (range / (float)NUM_SEGMENTS)); 
    		} else {
        		currCounter++; 
    		}
    		totalCounter++; 
    	}
    	
    	for (int i = 0; i < NUM_SEGMENTS; i++) {
    		if (i < segmentCounters.size()) {
    			segmentProportions.add((double)segmentCounters.get(i) / (double)totalCounter); 
    		} else {
    			segmentProportions.add(0.0); 
    		}
    	}
    }
    

    private void colorHeatmapSegment(int num) {
    	int idNum = num + 1; 
    	String id = "heat_rect_" + idNum; 
        int resID = this.getResources().getIdentifier(id, "id", getPackageName());
        View rectView = this.findViewById(resID); 
        
        int redColor = (int)(segmentProportions.get(num) * (float)1000); 
        
        int color = Color.rgb(redColor, 0, 0); 
        
        rectView.setBackgroundColor(color);
    }
}