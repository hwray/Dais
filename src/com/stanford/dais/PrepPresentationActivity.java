package com.stanford.dais; 

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

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

	private TextView mLeftHeadingView; 
	private TextView mRightHeadingView; 

    private static final float TOO_LONG_GAZE_TIME = 10.0f; 
    private static final float TOO_STEEP_PITCH_DEGREES = 10.0f;
    
    private OrientationManager mOrientationManager;        
    private boolean mInterference; 
    
    private GestureDetector mGestureDetector;
    
    private Thread mGazeThread;  

    /* FIREBASE GLOBALS */
     Firebase connection;
     Firebase testConnection;
	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
        
        }

        @Override
        public void onLocationChanged(OrientationManager orientationManager) {
        	// Do nothing
        }

        @Override
        public void onAccuracyChanged(OrientationManager orientationManager) {
            mInterference = orientationManager.hasInterference();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_presentation);
        
        this.g = (Globals) getApplication(); 
        
        g.clearGlobals(); 
        mInterference = false; 
        
        mMainView = (View) findViewById(R.id.prep_presentation_container); 
        mHeadingView = (TextView) findViewById(R.id.compass_heading); 
        mTitleView = (TextView) findViewById(R.id.instructions_and_feedback); 
        mLeftHeadingView = (TextView) findViewById(R.id.left_heading); 
        mRightHeadingView = (TextView) findViewById(R.id.right_heading); 
        
        mGestureDetector = createGestureDetector(this); 
        
        SensorManager sensorManager =
                (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mOrientationManager = new OrientationManager(sensorManager, locationManager);
        
        mOrientationManager.addOnChangedListener(mCompassListener);
        mOrientationManager.start();
        
        initFirebase(); 
        initGazeThread(); 
    }
    
    
    private GestureDetector createGestureDetector(Context context) {
    	g.pres = new Presentation();
    	
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
        	 @Override
             public boolean onGesture(Gesture gesture) {
             	
                 if (gesture == Gesture.TAP) {
                     // mAudioManager.playSoundEffect(Sounds.TAP);
                 	if (g.pres.mLeftHeading == 0) {
                 		g.pres.mLeftHeading = mOrientationManager.getHeading(); 
                 		mLeftHeadingView.setText("" + g.pres.mLeftHeading); 
                 		mTitleView.setText("Look at right side of room and tap"); 
                 	} else if (g.pres.mRightHeading == 0) {
                 		g.pres.mRightHeading = mOrientationManager.getHeading(); 
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
                 		g.pres.mCenterHeading = (g.pres.mLeftHeading + g.pres.mRightHeading) / 2; 
                 		
                 		mLeftHeadingView.setText("");
                 		mRightHeadingView.setText("");
                 		mHeadingView.setText(""); 
                 	}
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
                 	connection.setValue(g.pres.toMap());
                 	g.pres.reset();
                 }
                 return false;
        	 }
        });
        return gestureDetector;
    }
    
    public void initFirebase() {
    	connection = new Firebase("https://dais.firebaseio.com/demo/"); // Firebase
    	connection.setValue("Hello, World!");
    	testConnection = new Firebase("http://dais.firebaseio.com/testStatus");
    	testConnection.setValue("creating new gesture");
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

    private void initGazeThread() {
		mGazeThread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (mInterference) {
							mHeadingView.setText("Magnetic interference");
							continue; 
						}
						
			        	if (g.pres.mLeftHeading == 0 || g.pres.mRightHeading == 0) {
			            	mHeadingView.setText("" + mOrientationManager.getHeading());
			        	}
			        	
			        	if (mOrientationManager.getPitch() > TOO_STEEP_PITCH_DEGREES) {
			        		mTitleView.setText("Look up!"); 
			        	} else {
			        		float heading = mOrientationManager.getHeading(); 
			        		g.pres.headings.add(heading); 
			        		
			        		if (heading > g.pres.mLeftHeading && heading < g.pres.mCenterHeading) {
			        			if (g.pres.mGazeSide != 0) {
				        			g.pres.mGazeSide = 0; 
			        				g.pres.mGazeTime = 0; 
			        			}
			        			g.pres.mGazeTime += 1; 
			        			
			        			if (g.pres.mGazeTime > TOO_LONG_GAZE_TIME) {
			        				mTitleView.setText("Look right!"); 
			        			} else {
				        			mTitleView.setText("");
			        			}
			        			
			        			g.pres.mLeftTime += 1; 
			        		} 
			        		else if (heading > g.pres.mCenterHeading && heading < g.pres.mRightHeading) {
			        			if (g.pres.mGazeSide != 1) {
				        			g.pres.mGazeSide = 1; 
				        			g.pres.mGazeTime = 0; 
			        			}
			        			g.pres.mGazeTime += 1; 
			        			
			        			if (g.pres.mGazeTime < TOO_LONG_GAZE_TIME) {
			        				mTitleView.setText("Look left!");
			        			} else {
			        				mTitleView.setText(""); 
			        			}
			        			
			        			g.pres.mRightTime += 1; 
			        		} 
			        		else {
			        			mTitleView.setText("Blowing it"); 
			        		}
			        	}
						
						Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		mGazeThread.start(); 
    }
}