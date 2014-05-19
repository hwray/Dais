package com.stanford.dais; 

import java.util.ArrayList;
import java.util.List;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

import com.firebase.client.*;

public class PrepPresentationActivity extends Activity {

	private Globals g; 
	
	private View mMainView; 
	private TextView mTitleView; 
	private TextView mHeadingView; 
	
    private OrientationManager mOrientationManager;
    
    private static final float TOO_STEEP_PITCH_DEGREES = 10.0f;
    
    private GestureDetector mGestureDetector;
    
    /* FIREBASE GLOBALS */
     Firebase connection;
	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
        	if (g.pres.mLeftHeading == 0 || g.pres.mRightHeading == 0) {
            	mHeadingView.setText("" + orientationManager.getHeading());
        	} else if (Math.abs(orientationManager.getPitch()) > TOO_STEEP_PITCH_DEGREES) {
        		mTitleView.setText("Look Up!"); 
        	} else {
        		float orientation = orientationManager.getHeading();
        		g.pres.mCurrentHeading = orientation;
        		g.pres.orientations.add(orientation);
        		if (orientation > g.pres.mLeftHeading && orientation < g.pres.mRightHeading) {
        			mTitleView.setText(""); 
        		} else {
        			mTitleView.setText("Blowing it"); 
        		}
            	
        	}
        	//connection.setValue(Float.valueOf(pres.mCenterHeading));
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
        	// Do nothing
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
            //mInterference = orientationManager.hasInterference();
            //updateTipsView();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_presentation);
        
        this.g = (Globals) getApplication(); 
        
        g.pres.mLeftHeading = 0; 
        g.pres.mRightHeading = 0; 
        g.pres.orientations = new ArrayList<Float>(); 
        
        mMainView = (View) findViewById(R.id.prep_presentation_container); 
        mHeadingView = (TextView) findViewById(R.id.compass_heading); 
        mTitleView = (TextView) findViewById(R.id.instructions_and_feedback); 
        
        mGestureDetector = createGestureDetector(this); 
        
        SensorManager sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mOrientationManager = new OrientationManager(sensorManager, locationManager);
        
        mOrientationManager.addOnChangedListener(mCompassListener);
        mOrientationManager.start();
    }
    
    
    private GestureDetector createGestureDetector(Context context) {
    	
    	connection = new Firebase("https://dais.firebaseio.com/demo/"); // Firebase

    	g.pres = new Presentation(new Firebase("https://dais.firebaseio.com/demo"));
    	
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
            gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
                @Override
                public boolean onGesture(Gesture gesture) {
                	
                    if (gesture == Gesture.TAP) {
                        // mAudioManager.playSoundEffect(Sounds.TAP);
                    	if (g.pres.mLeftHeading == 0) {
                    		g.pres.mLeftHeading = mOrientationManager.getHeading(); 
                    		TextView leftHeadingView = (TextView) mMainView.findViewById(R.id.left_heading); 
                    		leftHeadingView.setText("" + g.pres.mLeftHeading); 
                    		mTitleView.setText("Look at right side of room and tap"); 
                    	} else if (g.pres.mRightHeading == 0) {
                    		g.pres.mRightHeading = mOrientationManager.getHeading(); 
                    		TextView rightHeadingView = (TextView) mMainView.findViewById(R.id.right_heading); 
                    		rightHeadingView.setText("" + g.pres.mRightHeading); 
                    		
                    		if (g.pres.mRightHeading < g.pres.mLeftHeading) {
                    			float temp = g.pres.mRightHeading; 
                    			g.pres.mRightHeading = g.pres.mLeftHeading; 
                    			g.pres.mLeftHeading = temp; 
                    		}
                    		
                    		g.pres.mCenterHeading = (g.pres.mLeftHeading + g.pres.mRightHeading) / 2; 
                    		
                    		mHeadingView.setText(""); 
                    	}
                        return true;
                    } else if (gesture == Gesture.TWO_TAP) {
                        // do something on two finger tap
                        return true;
                    } else if (gesture == Gesture.SWIPE_RIGHT) {
                        // do something on right (forward) swipe
                        return true;
                    } else if (gesture == Gesture.SWIPE_LEFT) {
                        // do something on left (backwards) swipe
                        return true;
                    } else if (gesture == Gesture.SWIPE_DOWN) {
                    	g.pres.pushOnline();
                    }
                    return false;
                }
            });
            return gestureDetector;
        }
    
    /*
     * Send generic motion events to the gesture detector
     */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

	
}