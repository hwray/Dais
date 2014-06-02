package com.stanford.dais; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewDataActivity extends Activity {
	
	private Globals g; 
	private View mainView; 
		
    private int NUM_CARDS = 3; 
    private ArrayList<Card> mCards; 
    
	private final int NUM_SEGMENTS = 25; 
	private ArrayList<Double> segmentProportions; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_data);
        
        g = (Globals) getApplication(); 
        
        mCards = new ArrayList<Card>(); 
        for (int i = 0; i < NUM_CARDS; i++) {
        	initializeCard(i); 
        }

        CardScrollView cardsView = new CardScrollView(this);
        CustomCardScrollAdapter csAdapter = new CustomCardScrollAdapter();
        cardsView.setAdapter(csAdapter);
        cardsView.activate();
        setContentView(cardsView);
    }
    
    
    private void initializeCard(int index) {
    	Card newCard = new Card(this); 
    	if (index == 0) {
        	newCard.setText("Visual Spread Heatmap"); 
    		//initializeVisualHeatmap(); 
    	} else if (index == 1) {
        	newCard.setText("Visual Spread Over Time");
    		//initializeSecond(); 
    	} else if (index == 2) {
    		newCard.setText("Voice Volume Over Time");
    	}
    	mCards.add(newCard); 
    }
    
    
    private void initializeVisualHeatmap() {
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
        
    	LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.activity_view_data, null, false);
        
        for (int i = 0; i < NUM_SEGMENTS; i++) {
        	colorHeatmapSegment(i, v);
        }
        
        //cardViews.add(v); 
    }
    
    
    private void initializeSecond() {
    	LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.activity_main, null, false);
        
        //cardViews.add(v); 
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
    

    private void colorHeatmapSegment(int num, View v) {
    	int idNum = num + 1; 
    	String id = "heat_rect_" + idNum; 
        int resID = this.getResources().getIdentifier(id, "id", getPackageName());
        View rectView = v.findViewById(resID); 
        
        int redColor = (int)(segmentProportions.get(num) * (float)1000); 
        
        int color = Color.rgb(redColor, 0, 0); 
        
        rectView.setBackgroundColor(color);
    }
    
    private class CustomCardScrollAdapter extends CardScrollAdapter {

        @Override
        public int getPosition(Object item) {
            return mCards.indexOf(item);
        }

        @Override
        public int getCount() {
            return mCards.size();
        }

        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }

        /**
         * Returns the amount of view types.
         */
        @Override
        public int getViewTypeCount() {
            return Card.getViewTypeCount();
        }

        /**
         * Returns the view type of this card so the system can figure out
         * if it can be recycled.
         */
        @Override
        public int getItemViewType(int position){
            return mCards.get(position).getItemViewType();
        }

        @Override
        public View getView(int position, View convertView,
                ViewGroup parent) {
            return  mCards.get(position).getView(convertView, parent);
        }
    }
}
