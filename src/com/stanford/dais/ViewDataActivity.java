package com.stanford.dais; 

import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ViewDataActivity extends Activity {
	
	//private Globals g; 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        
        Globals g = (Globals) getApplication(); 
        
        Collections.sort(g.orientations, new Comparator<Float>() {
            @Override
            public int compare(Float orientation1, Float  orientation2)
            {
            	if (orientation1 < orientation2) {
            		return -1; 
            	} else if (orientation1 > orientation2) {
            		return 1;
            	} else {
            		return 0; 
            	}
            }
        });
        
    }
}