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
	
    private final OrientationManager.OnChangedListener mCompassListener =
            new OrientationManager.OnChangedListener() {

        @Override
        public void onOrientationChanged(OrientationManager orientationManager) {
        	// Do nothing
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
        
        initGazeThread(); 
    }
    
    
    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
            //Create a base listener for generic gestures
            gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
                @Override
                public boolean onGesture(Gesture gesture) {
                    if (gesture == Gesture.TAP) {
                        // mAudioManager.playSoundEffect(Sounds.TAP);
                    	if (g.mHeadingLeft == 0) {
                    		g.mHeadingLeft = mOrientationManager.getHeading(); 
                    		mLeftHeadingView.setText("" + g.mHeadingLeft); 
                    		mTitleView.setText("Look at right side of room and tap"); 
                    	} else if (g.mHeadingRight == 0) {
                    		g.mHeadingRight = mOrientationManager.getHeading(); 
                    		
                    		if (g.mHeadingRight < g.mHeadingLeft) {
                    			float temp = g.mHeadingRight; 
                    			g.mHeadingRight = g.mHeadingLeft; 
                    			g.mHeadingLeft = temp; 
                    		}
                    		
                    		g.mHeadingCenter = (g.mHeadingLeft + g.mHeadingRight) / 2; 
                    		
                    		mLeftHeadingView.setText("");
                    		mRightHeadingView.setText("");
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

    private void initGazeThread() {
		mGazeThread = new Thread() {
			public void run() {
				while (true) {
					try {
						if (mInterference) {
							mHeadingView.setText("Magnetic interference");
							continue; 
						}
						
			        	if (g.mHeadingLeft == 0 || g.mHeadingRight == 0) {
			            	mHeadingView.setText("" + mOrientationManager.getHeading());
			        	}
			        	
			        	if (mOrientationManager.getPitch() > TOO_STEEP_PITCH_DEGREES) {
			        		mTitleView.setText("Look up!"); 
			        	} else {
			        		float orientation = mOrientationManager.getHeading(); 
			        		g.orientations.add(orientation); 
			        		
			        		if (orientation > g.mHeadingLeft && orientation < g.mHeadingCenter) {
			        			if (g.mGazeSide != 0) {
				        			g.mGazeSide = 0; 
			        				g.mGazeTime = 0; 
			        			}
			        			g.mGazeTime += 1; 
			        			
			        			if (g.mGazeTime > TOO_LONG_GAZE_TIME) {
			        				mTitleView.setText("Look right!"); 
			        			} else {
				        			mTitleView.setText("");
			        			}
			        			
			        			g.mTimeLeft += 1; 
			        		} 
			        		else if (orientation > g.mHeadingCenter && orientation < g.mHeadingRight) {
			        			if (g.mGazeSide != 1) {
				        			g.mGazeSide = 1; 
				        			g.mGazeTime = 0; 
			        			}
			        			g.mGazeTime += 1; 
			        			
			        			if (g.mGazeTime < TOO_LONG_GAZE_TIME) {
			        				mTitleView.setText("Look left!");
			        			} else {
			        				mTitleView.setText(""); 
			        			}
			        			
			        			g.mTimeRight += 1; 
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