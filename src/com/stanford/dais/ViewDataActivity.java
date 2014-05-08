package com.stanford.dais; 

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
        
        TextView dataView = (TextView) findViewById(R.id.data_title);
        dataView.setText("Left: " + g.mTimeLeft + " Right: " + g.mTimeRight); 
        
    }
}